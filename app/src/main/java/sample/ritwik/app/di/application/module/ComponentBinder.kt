package sample.ritwik.app.di.application.module

import androidx.lifecycle.ViewModel

import com.droidboi.common.mvvm.di.mapKey.ViewModelKey

import dagger.Binds
import dagger.Module

import dagger.android.ContributesAndroidInjector

import dagger.multibindings.IntoMap

import sample.ritwik.app.di.activity.MainModule

import sample.ritwik.app.mvvm.viewModel.MainViewModel

import sample.ritwik.app.ui.activity.MainActivity

/**
 * Abstract [Module] that consists of methods annotated with [ContributesAndroidInjector], [Binds],
 * [IntoMap] and [ViewModelKey].
 *
 * @author Ritwik Jamuar
 */
@Module
abstract class ComponentBinder {

    /*--------------------------------- Activity Binding Methods ---------------------------------*/

    /**
     * Provides the instance of [MainActivity].
     *
     * @return Instance of [MainActivity].
     */
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributesMainActivity(): MainActivity

    /*-------------------------------- ViewModel Binding Methods ---------------------------------*/

    /**
     * Provides the [ViewModel] from [MainViewModel].
     *
     * @param viewModel Instance of [MainViewModel].
     * @return Instance of [ViewModel].
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun providesMainViewModel(viewModel: MainViewModel): ViewModel

}
