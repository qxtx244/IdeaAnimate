package qxtx.idea.animate;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @CreateDate 2019/01/16 16:18.
 * @Author QXTX-GOSPELL
 */

public interface IManager<T> {
    void add(T animator);

    void remove(T animator);

    void remove(@NonNull String tag);

    void remove(int index);

    int getCount();

    List<T> getAnimateList();

    List<T> get(@NonNull String tag);

    void release();
}
