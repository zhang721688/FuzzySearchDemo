package com.zxn.fuzzysearch

import android.text.TextUtils
import android.widget.Filter
import android.widget.Filterable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*

/**
 * Updated by zxn on 2020/11/2.
 */
abstract class FuzzySearchBaseAdapter<ITEM : IFuzzySearchItem, K : BaseViewHolder>(
    rule: IFuzzySearchRule?,
    dataList: MutableList<ITEM>?,
    layoutResId: Int
) : BaseQuickAdapter<ITEM, K>(layoutResId, dataList), Filterable {
    private var mFilter: FuzzySearchFilter = FuzzySearchFilter()
    var backDataList: MutableList<ITEM>?
    private var mIFuzzySearchRule: IFuzzySearchRule? = null

    init {
        if (rule == null) {
            mIFuzzySearchRule = DefaultFuzzySearchRule()
        }
        backDataList = dataList
    }


    override fun setList(list: Collection<ITEM>?) {
        super.setList(list)
        backDataList = data
    }

    override fun getFilter(): Filter  = mFilter

    private inner class FuzzySearchFilter : Filter() {
        /**
         * 执行过滤操作,如果搜索的关键字为空，默认所有结果
         */
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val result = FilterResults()
            val filterList: MutableList<ITEM>?
            if (TextUtils.isEmpty(constraint)) {
                filterList = backDataList
            } else {
                filterList = ArrayList()
                for (item in backDataList!!) {
                    if (mIFuzzySearchRule!!.accept(constraint, item!!.sourceKey, item.fuzzyKey)) {
                        filterList.add(item)
                    }
                }
            }
            result.values = filterList
            result.count = filterList!!.size
            return result
        }

        /**
         * 得到过滤结果
         */
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            //setNewData(results.values as MutableList<ITEM>)
            if (results.values == null) {
                setList(null)
            } else {
                if (results.values is MutableList<*>) {
                    setList(results.values as MutableList<ITEM>)
                }
            }
            //notifyDataSetChanged()
        }
    }

    @Deprecated("使用setList代替")
    override fun setNewData(data: MutableList<ITEM>?) {
        super.setNewData(data)
        backDataList = data
    }

}