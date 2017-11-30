package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.dbcache.DbCacheArg;
import com.byoutline.cachedfield.dbcache.DbCachedValueProviderWithArg;
import com.byoutline.cachedfield.dbcache.DbWriterWithArg;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.ibuscachedfield.IBusCachedFieldWithArgBuilder;
import com.byoutline.ibuscachedfield.builders.CachedFieldWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import de.greenrobot.event.EventBus;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link EventBusCachedField}. If you do not like
 * fluent interface create {@link EventBusCachedField} by one of its constructors.
 *
 * @param <RETURN_TYPE> Type of object to be cached.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE> extends IBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE, EventBus, EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {

    public EventBusCachedFieldWithArgBuilder() {
        super(new ConstructorWrapper<RETURN_TYPE, ARG_TYPE>(),
                EventBusCachedField.defaultBus,
                EventBusCachedField.defaultSessionIdProvider,
                EventBusCachedField.defaultValueGetterExecutor,
                EventBusCachedField.defaultStateListenerExecutor);
    }


    public <API_RETURN_TYPE> DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE> withApiFetcher(ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider) {
        return new DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE>(apiValueProvider);
    }

    public static class DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE> {
        private final ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider;

        public DbCacheBuilderReader(ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider) {
            this.apiValueProvider = apiValueProvider;
        }

        public DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE> withDbWriter(DbWriterWithArg<API_RETURN_TYPE, ARG_TYPE> dbSaver) {
            return new DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE>(apiValueProvider, dbSaver);
        }
    }

    public static class DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE> {
        private final ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider;
        private final DbWriterWithArg<API_RETURN_TYPE, ARG_TYPE> dbSaver;

        public DbCacheBuilderWriter(ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider, DbWriterWithArg<API_RETURN_TYPE, ARG_TYPE> dbSaver) {
            this.apiValueProvider = apiValueProvider;
            this.dbSaver = dbSaver;
        }

        public IBusCachedFieldWithArgBuilder.SuccessEvent withDbReader(ProviderWithArg<RETURN_TYPE, ARG_TYPE> dbValueProvider) {
            ProviderWithArg<RETURN_TYPE, DbCacheArg<ARG_TYPE>> valueProvider = new DbCachedValueProviderWithArg<API_RETURN_TYPE, RETURN_TYPE, ARG_TYPE>(apiValueProvider, dbSaver, dbValueProvider);
            return new EventBusCachedFieldWithArgBuilder<RETURN_TYPE, DbCacheArg<ARG_TYPE>>()
                    .withValueProvider(valueProvider);
        }
    }

    private static class ConstructorWrapper<RETURN_TYPE, ARG_TYPE> implements CachedFieldWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, EventBus, EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {
        @Override
        public EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build(Provider<String> sessionIdProvider, ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter, ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent, ResponseEventWithArg<Exception, ARG_TYPE> errorEvent, EventBus eventBus, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
            return new EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>(sessionIdProvider, valueGetter,
                    successEvent, errorEvent,
                    new EventIBus(eventBus),
                    valueGetterExecutor, stateListenerExecutor);
        }
    }
}
