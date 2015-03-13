package com.pushtorefresh.android.bamboostorage.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;
import com.pushtorefresh.android.bamboostorage.db.operation.Changes;
import com.pushtorefresh.android.bamboostorage.db.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, DeleteQuery> mapFunc;

    protected PreparedDeleteObject(@NonNull BambooStorageDb bambooStorageDb, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(bambooStorageDb);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final BambooStorageDb.Internal internal = bambooStorageDb.internal();
        final DeleteQuery deleteQuery = mapFunc.map(object);

        final int countOfDeletedRows = internal.delete(deleteQuery);

        internal.notifyAboutChanges(new Changes(deleteQuery.table));

        return DeleteResult.newDeleteResult(countOfDeletedRows, Collections.singleton(deleteQuery.table));
    }

    @NonNull @Override public Observable<DeleteResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorageDb bambooStorageDb;
        @NonNull private final T object;

        private MapFunc<T, DeleteQuery> mapFunc;

        public Builder(@NonNull BambooStorageDb bambooStorageDb, @NonNull T object) {
            this.bambooStorageDb = bambooStorageDb;
            this.object = object;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<>(bambooStorageDb, object, mapFunc);
        }
    }
}