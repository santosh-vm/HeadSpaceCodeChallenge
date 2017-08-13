package android.santosh.com.headspacecodechallenge.recyclerviewadapters;

import android.content.Context;
import android.santosh.com.headspacecodechallenge.R;
import android.santosh.com.headspacecodechallenge.model.CellData;
import android.santosh.com.headspacecodechallenge.model.ColumnTitle;
import android.santosh.com.headspacecodechallenge.model.HeaderTitle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Santosh on 8/13/17.
 */

public class CustomAdapter extends MainExcelSheetRecyclerViewAdapter<HeaderTitle, ColumnTitle, CellData> {
    private Context context;
    private View.OnClickListener clickListener;

    public CustomAdapter(Context context, View.OnClickListener clickListener) {
        super(context);
        this.context = context;
        this.clickListener = clickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateCellDataViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_layout_view, parent, false);
        ContentViewHolder contentViewHolder = new ContentViewHolder(layout);
        return contentViewHolder;
    }

    @Override
    public void onBindCellDataViewHolder(RecyclerView.ViewHolder holder, int horizontalPosition, int verticalPosition) {
        CellData cellData = getContentItem(horizontalPosition, verticalPosition);
        if (null == holder || !(holder instanceof ContentViewHolder) || cellData == null) {
            return;
        }
        ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
        if (!TextUtils.isEmpty(cellData.getData())) {
            contentViewHolder.titleTextView.setText(cellData.getData());
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ContentViewHolder(View itemview) {
            super(itemview);
            titleTextView = itemview.findViewById(R.id.cell_content);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout_view, parent, false);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(layout);
        return headerViewHolder;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        HeaderTitle headerTitle = getHeaderItem(position);
        if (null == holder || !(holder instanceof HeaderViewHolder) || headerTitle == null) {
            return;
        }
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.titleTextView.setText(headerTitle.getTitle());

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public HeaderViewHolder(View itemview) {
            super(itemview);
            titleTextView = itemview.findViewById(R.id.cell_title);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateColumnViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout_view, parent, false);
        ColumnViewHolder columnViewHolder = new ColumnViewHolder(layout);
        return columnViewHolder;
    }

    @Override
    public void onBindColumnViewHolder(RecyclerView.ViewHolder holder, int position) {
        ColumnTitle columnTitle = getColumnItem(position);
        if (null == holder || !(holder instanceof ColumnViewHolder) || columnTitle == null) {
            return;
        }
        ColumnViewHolder columnViewHolder = (ColumnViewHolder) holder;
        columnViewHolder.titleTextView.setText(columnTitle.getTitle());
    }

    class ColumnViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ColumnViewHolder(View itemview) {
            super(itemview);
            titleTextView = itemview.findViewById(R.id.cell_title);
        }
    }

    @Override
    public View onCreateTopLeftView() {
        return LayoutInflater.from(context).inflate(R.layout.top_left_blank_view, null);
    }
}
