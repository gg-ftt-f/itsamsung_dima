package com.example.it_samsung_project_v1.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.adapter.MyLibAdapter;
import com.example.it_samsung_project_v1.adapter.SearchAdapter;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;
import com.example.it_samsung_project_v1.vm.BookViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class myLibFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView rvResults;

    private BookViewModel vm;
    private MyLibAdapter adapter;
    private Spinner spinner;
    private Button btClearFilter;
    ArrayAdapter<Status> spinneradapter;

    private List<Status> statuses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_lib_list, container, false);

        rvResults = view.findViewById(R.id.lib_list);
        vm = new ViewModelProvider(this).get(BookViewModel.class);

        spinner = view.findViewById(R.id.sp_spStatus);
        spinneradapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Status item = (Status)parent.getItemAtPosition(position);
                vm.getAllBooks(item).observe(getViewLifecycleOwner(), books -> {
                    adapter.setItems(books);
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinner.setOnItemSelectedListener(itemSelectedListener);

        btClearFilter = view.findViewById(R.id.bt_clearFilter);
        btClearFilter.setOnClickListener(v->{

            vm.getAllBooks().observe(getViewLifecycleOwner(), books -> {
                adapter.setItems(books);
            });
        });

        adapter = new MyLibAdapter(this::onItemLongClick);
        rvResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        rvResults.setAdapter(adapter);

        vm.getAllBooks().observe(getViewLifecycleOwner(), books -> {
            adapter.setItems(books);
        });

        vm.getAllStatuses().observe(getViewLifecycleOwner(), read_ststuses->{
            statuses.addAll(read_ststuses);
            adapter.setStatuses(read_ststuses);
            spinneradapter.addAll(read_ststuses);

        });

        return view;
    }

    private void onItemLongClick(SearchDoc doc, Book saved, String idKey, View anchor, String key) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        Menu menu = pm.getMenu();


            // 2) «Изменить статус»
            SubMenu sm = menu.addSubMenu("Изменить статус");

            if(statuses.size() == 0){
                sm.add("Заполните список статусов!!");
            }
            for(Status stat:statuses){
                MenuItem newItem = sm.add(stat.caption);
                int newStatId = stat.status_id;
                newItem.setOnMenuItemClickListener(i -> {
                    saved.status_ID = newStatId;
                    vm.insertOrUpdate(saved);
                    return true;
                });
            }

//            sm.add("В планах").setOnMenuItemClickListener(i -> {
//                saved.status_ID = 1;
//                vm.insertOrUpdate(saved);
//                return true;
//            });
//            sm.add("Читаю").setOnMenuItemClickListener(i -> {
//                saved.status_ID = 2;
//                vm.insertOrUpdate(saved);
//                return true;
//            });
//            sm.add("Прочитано").setOnMenuItemClickListener(i -> {
//                saved.status_ID = 3;
//                vm.insertOrUpdate(saved);
//                return true;
//            });
            menu.add("Удалить из библиотеки").setOnMenuItemClickListener(i -> {
                vm.deleteBook(saved);
                return true;
            });
        pm.show();
    }



}