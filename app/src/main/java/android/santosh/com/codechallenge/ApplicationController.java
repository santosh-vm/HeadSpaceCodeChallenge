package android.santosh.com.codechallenge;

import android.os.Handler;
import android.santosh.com.codechallenge.interfaces.ExcelSheetListener;
import android.santosh.com.codechallenge.model.ColumnTitle;
import android.santosh.com.codechallenge.model.HeaderTitle;
import android.santosh.com.codechallenge.model.TableData;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Santosh on 8/13/17.
 */

public class ApplicationController {
    private static String TAG = ApplicationController.class.getSimpleName();
    private String alphabets = "abcdefghijklmnopqrstuvwxyz";
    private static int ROW_SIZE = 8;
    private static int COLUMN_SIZE = 8;
    private int currentlySelectedRowIndex = 0;
    private int currentlySelectedColumnIndex = 0;

    private Handler uiHandler;
    private SharedPreferencesWrapper sharedPreferencesWrapper;
    private Gson gson;
    private ExecutorService executorService;
    private List<List<TableData.CellData>> tableDataList;
    private List<HeaderTitle> headerTitleList;
    private List<ColumnTitle> columnTitleList;
    private List<ExcelSheetListener> excelSheetListeners = Collections.synchronizedList(new ArrayList<ExcelSheetListener>());

    public ApplicationController(Handler uiHandler, SharedPreferencesWrapper sharedPreferencesWrapper) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.uiHandler = uiHandler;
        this.gson = new GsonBuilder().create();
        this.sharedPreferencesWrapper = sharedPreferencesWrapper;
    }

    public void fetchExcelSheetData() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (tableDataList == null || tableDataList.size() < 0) {
                        generateHeaderData();
                        generateColumnData();
                        loadExcelSheetData();
                    }
                    notifyExcelSheetLoaded();
                }
            });
        }

    }

    public void saveExcelSheetData(){
        if(executorService!=null && !executorService.isShutdown()){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(tableDataList!=null && tableDataList.size()>0){
                        clearSelected();
                        String excelDataString = gson.toJson(tableDataList,tableDataList.getClass());
                        sharedPreferencesWrapper.saveExcelSheetDataAsString(excelDataString);
                        notifyExcelSheetCellDataRefresh();
                    }
                }
            });
        }
    }

    public void clearExcelSheet(){
        if(executorService!=null && !executorService.isShutdown()){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(tableDataList!=null && tableDataList.size()>0){
                        for(List<TableData.CellData> rowData : tableDataList){
                            for(TableData.CellData cellData : rowData){
                                cellData.setData(null);
                                cellData.setSelected(false);
                            }
                        }
                    }
                    currentlySelectedRowIndex = 0;
                    currentlySelectedColumnIndex = 0;
                    notifyExcelSheetCellDataRefresh();
                }
            });
        }
    }

    private synchronized void clearSelected(){
        if(tableDataList!=null && tableDataList.size()>0){
            for(List<TableData.CellData> rowData : tableDataList){
                for(TableData.CellData cellData : rowData){
                    if(cellData.isSelected()){
                        cellData.setSelected(false);
                    }
                }
            }
        }
        currentlySelectedRowIndex = 0;
        currentlySelectedColumnIndex = 0;
    }

    public synchronized void reloadExcelSheet(){
        if(executorService!=null && !executorService.isShutdown()){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    generateHeaderData();
                    generateColumnData();
                    loadExcelSheetData();
                    notifyExcelSheetLoaded();
                }
            });
        }
    }

    public void updateCellSelectedStatus(final int row, final int column) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (tableDataList != null && tableDataList.size() > 0) {
                        //Un-select previously selected Cell Data
                        tableDataList.get(currentlySelectedRowIndex).get(currentlySelectedColumnIndex).setSelected(false);
                        //Set select for Cell Data with the new row and column value.
                        tableDataList.get(row).get(column).setSelected(true);
                        currentlySelectedRowIndex = row;
                        currentlySelectedColumnIndex = column;
                        notifyExcelSheetCellDataRefresh();
                    }
                }
            });
        }
    }

    public void updateCellData(final String data, final int row, final int column) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (tableDataList != null && tableDataList.size() > 0) {
                        tableDataList.get(row).get(column).setData(data);
                        currentlySelectedRowIndex = row;
                        notifyExcelSheetCellDataRefresh();
                    }
                }
            });
        }
    }

    private void generateHeaderData() {
        headerTitleList = new LinkedList<>();
        for (int i = 0; i < COLUMN_SIZE; i++) {
            HeaderTitle headerTitle = new HeaderTitle();
            headerTitle.setTitle(Character.toString(Character.toUpperCase(alphabets.charAt(i))));
            headerTitleList.add(headerTitle);
        }
    }

    private void generateColumnData() {
        columnTitleList = new LinkedList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            ColumnTitle columnTitle = new ColumnTitle();
            columnTitle.setTitle(Integer.toString(i));
            columnTitleList.add(columnTitle);
        }
    }

    private void loadExcelSheetData() {
        tableDataList = new LinkedList<>();
        String excelDataAsString = sharedPreferencesWrapper.getExcelSheetDataAsString();
        if (TextUtils.isEmpty(excelDataAsString)) {
            generateDefaultCellData();
        } else {
            tableDataList = gson.fromJson(excelDataAsString, new TypeToken<List<List<TableData.CellData>>>() {}.getType());
        }
    }

    private void generateDefaultCellData() {
        tableDataList = new LinkedList<>();
        TableData tableData = new TableData();
        for (int i = 0; i < ROW_SIZE; i++) {
            List<TableData.CellData> cellDataList = new LinkedList<>();
            tableDataList.add(cellDataList);
            for (int j = 0; j < COLUMN_SIZE; j++) {
                TableData.CellData cellData = tableData.new CellData();
                cellData.setSelected(false);
                cellData.setData(null);
                cellDataList.add(cellData);
            }
        }
    }

    public void addExcelSheetListener(ExcelSheetListener excelSheetListener) {
        if (excelSheetListener != null && !excelSheetListeners.contains(excelSheetListener)) {
            excelSheetListeners.add(excelSheetListener);
        }
    }

    public void removeExcelSheetListener(ExcelSheetListener excelSheetListener) {
        if (excelSheetListener != null && excelSheetListeners.contains(excelSheetListener)) {
            excelSheetListeners.remove(excelSheetListener);
        }
    }

    private void notifyExcelSheetLoaded() {
        if (excelSheetListeners != null & excelSheetListeners.size() > 0) {
            for (final ExcelSheetListener excelSheetListener : excelSheetListeners) {
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "headerTitleList.size(): " + headerTitleList.size() + ", columnTitleList.size(): " + columnTitleList.size() + ", tableDataList.size(): " + tableDataList.size());
                        excelSheetListener.onExcelSheetLoaded(headerTitleList, columnTitleList, tableDataList);
                    }
                }, 1000);
            }
        }
    }

    private void notifyExcelSheetCellDataRefresh() {
        if (excelSheetListeners != null & excelSheetListeners.size() > 0) {
            for (final ExcelSheetListener excelSheetListener : excelSheetListeners) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        excelSheetListener.onExcelSheetCellDataRefreshed(tableDataList);
                    }
                });
            }
        }
    }
}
