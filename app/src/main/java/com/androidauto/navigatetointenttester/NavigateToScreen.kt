package com.androidauto.navigatetointenttester

import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.model.SearchTemplate.SearchCallback
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.androidauto.navigatetointenttester.db.NavigateTo

/**
 *
 */
class NavigateToScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver, NavigateToScreenCallback {

    val presenter = NavigateToScreenPresenter(this)

    init {
        lifecycle.addObserver(this)
        presenter.init()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        presenter.onDestroy()
    }

    override fun onGetTemplate(): Template {
        return SearchTemplate.Builder(
            object : SearchCallback {
                override fun onSearchTextChanged(searchText: String) {}
                override fun onSearchSubmitted(searchTerm: String) {
                    presenter.onSearchSubmitted(searchTerm)
                }
            })
            .setHeaderAction(Action.BACK)
            .setActionStrip(ActionStrip.Builder().addAction(
                Action.Builder()
                    .setTitle("X").setOnClickListener(
                        OnClickListener {
                            presenter.deleteLast()
                        }
                    ).build()).build())
            .setShowKeyboardByDefault(false)
            .setItemList(getListOfRowItems(presenter.getItemsToDisplay()))
            .setSearchHint("Street+1234,+City,+Country")
            .build()
    }

    private fun getListOfRowItems(listOfItems: List<NavigateTo>): ItemList {
        var itemsListBuilder = ItemList.Builder()
        itemsListBuilder.setNoItemsMessage("No items")
        listOfItems.forEach {
            itemsListBuilder.addItem(
                Row.Builder()
                    .setTitle(it?.term ?: "")
                    .setOnClickListener { presenter.updateItem(it); presenter.onItemClicked(it) }
                    .build())
        }
        return itemsListBuilder.build()
    }

    override fun context() = carContext

    override fun startCarApp(intent: Intent) = carContext.startCarApp(intent)

    override fun showToast(s: String) = CarToast.makeText(carContext, s, CarToast.LENGTH_LONG).show();

    override fun invalidateList() = invalidate()
}