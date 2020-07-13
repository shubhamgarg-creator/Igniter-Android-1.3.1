package igniter.dependencies.module;
/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.module
 * @category ApplicationModule
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/*****************************************************************
 Application Module
 ****************************************************************/
@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application application() {
        return application;
    }
}
