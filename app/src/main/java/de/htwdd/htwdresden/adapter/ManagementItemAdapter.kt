package de.htwdd.htwdresden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.ui.models.ManagementItem
import de.htwdd.htwdresden.ui.models.Managementable
import de.htwdd.htwdresden.ui.models.ManagementableModels
import de.htwdd.htwdresden.ui.models.SemesterPlanItem
import de.htwdd.htwdresden.utils.extensions.debug
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

typealias Managements           = ArrayList<Managementable>
typealias ManagementBindables   = ArrayList<Pair<Int, ManagementableModels>>

class ManagementItemAdapter(private val items: Managements): RecyclerView.Adapter<ManagementItemAdapter.ViewHolder>() {

    private val disposable = CompositeDisposable()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = item(position).let { item -> holder.bind(item.bindingTypes(), item) }

    override fun getItemViewType(position: Int) = item(position).itemViewType()

    fun item(onPosition: Int) = items[onPosition]

    fun update(items: Managements) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        internal fun bind(bindingItems: ManagementBindables, item: Managementable) {
            Observable
                .fromArray(bindingItems)
                .runInThread()
                .map { bindingItem ->
                    bindingItem.map { binding.setVariable(it.first, it.second) }
                }
                .runInUiThread()
                .subscribe {
                    binding.executePendingBindings()

                    when (item) {
                        is SemesterPlanItem -> {
                            item.addAdditionalInfo(binding.root.findViewById(R.id.llAdditionalInfo))
                        }
                        is ManagementItem -> {
                            item.offers(binding.root.findViewById(R.id.llOffers))
                            item.times(binding.root.findViewById(R.id.llTimes))
                        }
                        else -> {}
                    }
                }.addTo(disposable)


        }
    }
}