/*
 *
 * 一些开源的RecyclerView的LayoutManager
 * 1、FanLayoutManager：https://github.com/Cleveroad/FanLayoutManager 扇叶转动
 * 2、CarouselLayoutManager：https://github.com/Azoft/CarouselLayoutManager 传送带（卡片轮播）效果
 * 3、ChipsLayoutManager：https://github.com/BelooS/ChipsLayoutManager 流式布局效果（标签云）
 * 4、HiveLayoutManager：https://github.com/Chacojack/HiveLayoutManager 蜂巢效果（国人作品）
 * 5、vLayout：https://github.com/alibaba/vlayout 布局混排效果（天猫app所使用）
 * 6、flexbox-layout https://github.com/google/flexbox-layout flexbox效果（谷歌的东西，原本不支持recyclerView）
 * 7、LondonEyeLayoutManager https://github.com/danylovolokh/LondonEyeLayoutManager 环形菜单效果
 */

package org.qxtx.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for recyclerView. It can put a view list with item of multi style, and the {@link MyHolder} can
 * set item easily is the adapter have. Other hands, it provide interface {@link MultiLayout} and a default
 * item Decoration {@link DefaultItemDecoration}. You can change item style in any time but it may not be a good
 * idea to do that.
 * @param <T>   A type convert to adapter.
 */
public abstract class GenericsAdapter<T> extends RecyclerView.Adapter<GenericsAdapter<T>.MyHolder> {
    private static final String TAG = "GenericsAdapter";

    private final WeakReference<Context> context;
    private List<T> data;
    private int layoutId;
    private MultiLayout multiLayout;

    abstract void onBind(MyHolder viewHolder, int pos, List<T> data);

    public GenericsAdapter(Context context) {
        this(context, -1, null);
    }

    public GenericsAdapter(Context context, int layoutId, List<T> data) {
        this.context = new WeakReference<>(context);
        this.layoutId = layoutId;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    public GenericsAdapter(Context context, MultiLayout multiLayout, List<T> data) {
        this.context = new WeakReference<>(context);
        this.multiLayout = multiLayout;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    /**
     * Return a item id from the position on the item list while {@link #multiLayout} is not null,
     * and return {@link #layoutId} when it was reValue or return 0 with others.
     * @param position position for a item
     * @return item id for the item
     */
    @Override
    public int getItemViewType(int position) {
        if (multiLayout != null) {
            return multiLayout.getLayoutId(position);
        } else if (layoutId != -1) {
            return layoutId;
        }

        return super.getItemViewType(position);
    }

    /**
     * It will call {@link #getItemViewType(int)} to get a viewType.
     * @param viewType  It was result from {@link #getItemViewType(int)}
     */
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (multiLayout != null) {
            layoutId = viewType;
        }

        if (context == null || context.get() == null) {
            return null;
        }

        View itemView = null; 
        try {
            itemView = LayoutInflater.from(context.get()).inflate(layoutId, parent, false);
        } catch (Exception i) {
            Log.e(TAG, "inflate fail");
            itemView = new View(context.get());
            itemView.setId(View.generateViewId());
            return new MyHolder(itemView);
        }
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, int position) {
        onBind(viewHolder, position, data);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public long getItemId(int position) {
        return multiLayout == null ? layoutId : multiLayout.getLayoutId(position);
    }

    /**
     * If {@link #multiLayout} is non-null, it will be useless.
     * @param layoutId item id
     */
    public GenericsAdapter<T> setLayout(int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    /**
     * It will invalidate {@link #layoutId}.
     */
    public GenericsAdapter<T> setLayout(MultiLayout multiLayout) {
        this.multiLayout = multiLayout;
        return this;
    }

    /**
     * It will invalidate {@link #data} also refresh the view list.
     * @param data  Data list
     * @return  {@link GenericsAdapter} The Object that call with this
     */
    public GenericsAdapter<T> setListData(List<T> data) {
        if (this.data == null) {
            this.data = data;
        } else if (this.data != data) {
            this.data.clear();
            notifyDataSetChanged();
            this.data = data;
        }

        return this;
    }

    /**
     * ViewHolder that get item view and set something for item.
     */
    public final class MyHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> views;
        private View viewItem;

        MyHolder(View itemView) {
            super(itemView);

            views = new SparseArray<>();
            viewItem = itemView;
        }

        /**
         * Get subView from the item layout.
         */
        <T extends View>T getView(int viewId) {
            View v = views.get(viewId);
            if (v == null) {
                v = viewItem.findViewById(viewId);
                views.append(viewId, v);
            }

            return (T)v;
        }

        /*/*************************** Some public method ********************************/

        /**
         * Set listener for the view search by a viewId on the current item.
         * @param viewId view of item layout
         * @param listener Listener object
         * @return Result of set a listener for a item view.
         */
        public boolean setListener(int viewId, Object listener) {
            View v = checkView(viewId);
            String listenerCategory = checkAllParent(listener);
            if (listenerCategory == null) {
                return false;
            }

            try {
                Class<?> clazz =  Class.forName(listenerCategory);
                Method setter =  v.getClass().getMethod("set" + listenerCategory.split("\\$")[1], (Class)clazz);
                setter.invoke(v, listener);
            } catch (Exception e) {
                Log.e(TAG, "Fail to setListener: " + e.getMessage());
                return false;
            }

            return true;
        }

        public void setText(int viewId, CharSequence text) {
            View v = checkView(viewId);
            if (v != null && (v instanceof TextView)) {
                ((TextView)v).setText(text);
            }
        }

        public void setText(int viewId, int resId) {
            View v = checkView(viewId);
            if (v != null && (v instanceof TextView)) {
                ((TextView)v).setText(resId);
            }
        }

        public void setBackground(int viewId, Drawable backgroud) {
            View v = checkView(viewId);
            if (v != null) {
                v.setBackground(backgroud);
            }
        }

        public void setBackgroudColor(int viewId, int color) {
            View v = checkView(viewId);
            if (v != null) {
                v.setBackgroundColor(color);
            }
        }

        public void setBackgroudResource(int viewId, int resId) {
            View v = checkView(viewId);
            if (v != null) {
                v.setBackgroundResource(resId);
            }
        }

        public void setImageDrawable(int viewId, Drawable drawable) {
            View v = checkView(viewId);
            if (v != null && (v instanceof ImageView)) {
                ((ImageView)v).setImageDrawable(drawable);
            }
        }

        public void setImageResource(int viewId, int resId) {
            View v = checkView(viewId);
            if (v != null && (v instanceof ImageView)) {
                ((ImageView)v).setImageResource(resId);
            }
        }

        public void setImageBitmap(int viewId, Bitmap bm) {
            View v = checkView(viewId);
            if (v != null && (v instanceof ImageView)) {
                ((ImageView)v).setImageBitmap(bm);
            }
        }

        private View checkView(int viewId) {
            View v = getView(viewId);
            if (v == null) {
                Log.e(TAG, "Not found view for resource id[" + viewId + "]");
            }
            return v;
        }

        private String checkAllParent(Object obj) {
            if (obj == null) {
                return null;
            }

            Class<?> clazz = obj.getClass();
            //find interface that is name of "OnXXXListener"
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length != 0) {
                for (Class<?> anInterface : interfaces) {
                    String name = anInterface.getName();
                    if (name.contains("android.view.View$On") && name.endsWith("Listener")) {
                        return name;
                    }
                }
            }

            Class<?> parents = clazz.getSuperclass();
            return parents == null ? null : checkAllParent(parents);
        }
    }

    /**
     * Default item decoration implement. Auto value is (2, 2, 0, 0).
     */
    public static final class DefaultItemDecoration extends RecyclerView.ItemDecoration {
        private int left = 2;
        private int top = 2;
        private int right = 0;
        private int bottom = 0;

        public DefaultItemDecoration() {}

        public DefaultItemDecoration(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(left, top, right, bottom);
        }
    }

    /**
     * It can be used to set a multi type list.
     */
    public interface MultiLayout {
        int getLayoutId(int pos);
    }
}
