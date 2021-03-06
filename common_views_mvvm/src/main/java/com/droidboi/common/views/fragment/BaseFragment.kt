package com.droidboi.common.views.fragment

import android.content.Context

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import androidx.lifecycle.Observer

import com.droidboi.common.mvvm.model.BaseModel

import com.droidboi.common.mvvm.utility.ACTION_NONE
import com.droidboi.common.mvvm.utility.ACTION_PROGRESS_BAR
import com.droidboi.common.mvvm.utility.ACTION_UPDATE_UI

import com.droidboi.common.mvvm.viewModel.BaseViewModel

import com.droidboi.common.views.activity.BaseActivity

import java.lang.RuntimeException

/**
 * Abstract [Fragment] for handling common set-up required to show a [Fragment] in the UI.
 *
 * @param Binding Any Class referencing the View/Data Binding class of this [Fragment].
 * @author Ritwik Jamuar.
 */
abstract class BaseFragment<Model : BaseModel, ViewModel : BaseViewModel<Model>, Binding>
    : Fragment() {

    /*---------------------------------------- Components ----------------------------------------*/

    /**
     * Reference of [Binding] to control the Views under it.
     */
    protected var binding: Binding? = null

    /**
     * Reference of [ViewModel] as the ViewModel of [BaseActivity].
     */
    protected var viewModel: ViewModel? = null

    /*---------------------------------------- Observers -----------------------------------------*/

    /**
     * [Observer] of [BaseViewModel.uiLiveData].
     */
    private val uiObserver = Observer<Model> { uiData ->
        processUpdateOnUIData(uiData)
    }

    /*------------------------------------ Fragment Callbacks ------------------------------------*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<out BaseModel, out BaseViewModel<out BaseModel>, *>) {
            viewModel = context.getVM() as ViewModel
        } else {
            throw RuntimeException("$context must be an extension of ${BaseActivity::class.java}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            extractArguments(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = provideBinding(inflater, container)
        return provideViewRoot(binding!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        viewModel?.uiLiveData?.observe(viewLifecycleOwner, uiObserver)
    }

    override fun onPause() {
        super.onPause()
        hideLoading()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanUp()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        viewModel = null
    }

    /*-------------------------------------- Private Methods -------------------------------------*/

    /**
     * Handles the change in [Model] propagated from [viewModel].
     *
     * @param uiData Modified [Model].
     */
    private fun processUpdateOnUIData(uiData: Model): Unit = when (uiData.action) {

        ACTION_PROGRESS_BAR -> with(uiData.progressData) {
            if (showProgress) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        ACTION_UPDATE_UI -> onUIDataChanged(uiData)

        ACTION_NONE -> Unit

        else -> onAction(uiData)

    }

    /*------------------------------------- Abstract Methods -------------------------------------*/

    protected abstract fun provideBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Binding

    protected abstract fun provideViewRoot(binding: Binding): View

    /**
     * Tells this [BaseFragment] to extract the arguments from [Bundle].
     *
     *
     * This method will be executed only if the [Bundle] argument was set during the instantiation
     * of this [BaseFragment].
     *
     * @param arguments [Bundle] that contains the arguments.
     */
    protected abstract fun extractArguments(arguments: Bundle)

    /**
     * Tells this [BaseFragment] to perform initialization of it's [View]s through [binding].
     */
    protected abstract fun initializeViews()

    /**
     * Tells this [BaseFragment] to show the Loading.
     */
    protected abstract fun showLoading()

    /**
     * Tells this [BaseFragment] to hide the Loading.
     */
    protected abstract fun hideLoading()

    /**
     * Tells this [BaseFragment] that there is a change in UI Data.
     *
     * @param uiData Instance of [Model] changed from [viewModel].
     */
    protected abstract fun onUIDataChanged(uiData: Model)

    /**
     * Tells this [BaseFragment] to perform some action received from [ViewModel].
     */
    protected abstract fun onAction(uiData: Model)

    /**
     * Tells this [BaseFragment] to perform Clean-Up procedures for avoiding Memory Leaks.
     */
    protected abstract fun cleanUp()

}
