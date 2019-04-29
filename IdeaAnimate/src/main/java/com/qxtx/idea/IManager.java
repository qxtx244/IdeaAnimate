package com.qxtx.idea;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * CreatedDate   2019/01/16 16:18.
 * Author  QXTX-GOSPELL
 */

public interface IManager<T> {
    void add(T idea);

    void remove(T idea);

    void remove(@NonNull String tag);

    void remove(int index);

    int getCount();

    List<T> getAnimateList();

    List<T> get(@NonNull String tag);

    void release();
}
