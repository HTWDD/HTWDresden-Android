package de.htwdd.htwdresden.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.adapter.MealItemAdapter
import de.htwdd.htwdresden.adapter.Meals
import de.htwdd.htwdresden.ui.viewmodels.fragments.MealsViewModel
import de.htwdd.htwdresden.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_meals.*
import kotlinx.android.synthetic.main.layout_empty_view.*
import java.util.*

class MealsFragment: Fragment() {

    companion object {
        private const val ARG_TYPE  = "type"
        private const val ARG_ID    = "id"

        sealed class Type {
            object Today: Type()
            object Week: Type()
            object NextWeek: Type()
        }

        fun newInstance(type: Type, id: Int) = MealsFragment().apply {
            arguments = bundleOf(ARG_TYPE to when(type) {
                is Type.Today       -> "today"
                is Type.Week        -> "week"
                is Type.NextWeek    -> "nextWeek"
            }, ARG_ID to id)
        }
    }

    private val viewModel by lazy { getViewModel<MealsViewModel>() }
    private lateinit var mealItemAdapter: MealItemAdapter
    private val mealItems: Meals = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_meals, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        request()
    }

    private fun setup() {
        mealItemAdapter = MealItemAdapter(mealItems)
        mealRecycler.adapter = mealItemAdapter
        mealItemAdapter.onEmpty {
            weak { self ->
                self.includeEmptyLayout.toggle(it)
                self.tvIcon.text = "🍽"
                self.tvTitle.text = getString(R.string.mensa_no_meals)
                self.tvMessage.text = getString(if ((arguments?.getString(ARG_TYPE) ?: "today") == "today" ) R.string.mensa_no_offer_day else R.string.mensa_no_offer_week)
            }
        }
    }

    private fun request() {
        viewModel.type = arguments?.getString(ARG_TYPE) ?: "today"
        viewModel
            .request(arguments?.getInt(ARG_ID) ?: 80)
            .runInUiThread()
            .subscribe({ meals ->
                weak { self ->
                    self.mealItemAdapter.update(meals)
                }
            }, {
                error(it)
            })
            .addTo(disposeBag)
    }
}