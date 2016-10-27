package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.dbcache.DbCachedValueProvider;
import com.byoutline.cachedfield.dbcache.DbWriter;
import com.byoutline.cachedfield.dbcache.FetchType;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.IBusCachedFieldBuilder;
import com.byoutline.ibuscachedfield.builders.CachedFieldConstructorWrapper;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
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
public class EventBusCachedFieldBuilder<RETURN_TYPE> extends IBusCachedFieldBuilder<RETURN_TYPE, EventBus, EventBusCachedField<RETURN_TYPE>> {


    public EventBusCachedFieldBuilder() {
        super(new ConstructorWrapper<RETURN_TYPE>(),
                EventBusCachedField.defaultBus,
                EventBusCachedField.defaultSessionIdProvider,
                EventBusCachedField.defaultValueGetterExecutor,
                EventBusCachedField.defaultStateListenerExecutor);
    }


    public <API_RETURN_TYPE> DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> withApiFetcher(Provider<API_RETURN_TYPE> apiValueProvider) {
        return new DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider);
    }

    public static class DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;

        public DbCacheBuilderReader(Provider<API_RETURN_TYPE> apiValueProvider) {
            this.apiValueProvider = apiValueProvider;
        }

        public DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> withDbWriter(DbWriter<API_RETURN_TYPE> dbSaver) {
            return new DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider, dbSaver);
        }
    }

    public static class DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;
        private final DbWriter<API_RETURN_TYPE> dbSaver;

        public DbCacheBuilderWriter(Provider<API_RETURN_TYPE> apiValueProvider, DbWriter<API_RETURN_TYPE> dbSaver) {
            this.apiValueProvider = apiValueProvider;
            this.dbSaver = dbSaver;
        }

        public EventBusCachedFieldWithArgBuilder.SuccessEvent withDbReader(Provider<RETURN_TYPE> dbValueProvider) {
            ProviderWithArg<RETURN_TYPE, FetchType> valueProvider =
                    new DbCachedValueProvider<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider, dbSaver, dbValueProvider);
            return new EventBusCachedFieldWithArgBuilder<RETURN_TYPE, FetchType>()
                    .withValueProvider(valueProvider);
        }
    }

    private static class ConstructorWrapper<RETURN_TYPE> implements CachedFieldConstructorWrapper<RETURN_TYPE, EventBus, EventBusCachedField<RETURN_TYPE>> {
        @Override
        public EventBusCachedField<RETURN_TYPE> build(Provider<String> sessionIdProvider, Provider<RETURN_TYPE> valueGetter, ResponseEvent<RETURN_TYPE> successEvent, ErrorEvent errorEvent, EventBus eventBus, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
            return new EventBusCachedField<RETURN_TYPE>(sessionIdProvider, valueGetter,
                    successEvent, errorEvent,
                    new EventIBus(eventBus),
                    valueGetterExecutor, stateListenerExecutor);
        }
    }
}
