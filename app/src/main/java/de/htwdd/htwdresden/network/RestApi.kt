package de.htwdd.htwdresden.network

import android.annotation.SuppressLint
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import de.htwdd.htwdresden.network.endpoints.*
import de.htwdd.htwdresden.utils.holders.ResourceHolder
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RestApi {

    private const val WW2_URL = "https://www2.htw-dresden.de/~app/API/"
    private const val DOCS_URL = "https://rubu2.rz.htw-dresden.de/api/v1/docs/"
    private const val RUBU_URL = "https://rubu2.rz.htw-dresden.de/API/v0/"
    private const val QIS_URL = "https://wwwqis.htw-dresden.de/appservice/v2/"
    private const val MENSA_URL = "https://openmensa.org/api/v2/"

    private val rh: ResourceHolder by lazy { ResourceHolder.instance }
    private const val cacheSize: Long = 10L * (1024L * 1024L)

    private val safeOrUnsafeClient: OkHttpClient
        get() {
            return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                unsafeOkHttpClient(Cache(rh.getCacheDirectory(), cacheSize))
            } else {
                OkHttpClient.Builder()
                    .cache(Cache(rh.getCacheDirectory(), cacheSize)).build()
            }
        }

    val docsEndpoint: DocsEndpoint by lazy {
        val gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(DOCS_URL)
            .client(safeOrUnsafeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(DocsEndpoint::class.java)

    }

    val timetableEndpoint: TimetableEndpoint by lazy {
        val gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(RUBU_URL)
            .client(safeOrUnsafeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(TimetableEndpoint::class.java)

    }

    val examEndpoint: ExamEndpoint by lazy {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        }.create()

        val retrofit = Retrofit.Builder()
            .baseUrl(WW2_URL)
            .client(safeOrUnsafeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(ExamEndpoint::class.java)

    }

    val generalEndpoint: GeneralEndpoint by lazy {
        val gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(RUBU_URL)
            .client(safeOrUnsafeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(GeneralEndpoint::class.java)

    }

    val courseEndpoint: CourseEndpoint by lazy {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(QIS_URL)
            .client(OkHttpClient.Builder().cache(Cache(rh.getCacheDirectory(), cacheSize)).build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(CourseEndpoint::class.java)
    }

    val gradeEndpoint: GradeEndpoint by lazy {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(QIS_URL)
            .client(OkHttpClient.Builder().cache(Cache(rh.getCacheDirectory(), cacheSize)).build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(GradeEndpoint::class.java)
    }

    val canteenEndpoint: CanteenEnpoint by lazy {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(MENSA_URL)
            .client(unsafeOkHttpClient(Cache(rh.getCacheDirectory(), cacheSize)))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(CanteenEnpoint::class.java)
    }

    private fun unsafeOkHttpClient(cache: Cache? = null): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }.build()
    }
}