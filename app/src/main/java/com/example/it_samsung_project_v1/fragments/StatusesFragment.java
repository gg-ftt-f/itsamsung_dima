package com.example.it_samsung_project_v1.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.adapter.MyLibAdapter;
import com.example.it_samsung_project_v1.adapter.StatusAdapter;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;
import com.example.it_samsung_project_v1.vm.BookViewModel;


public class StatusesFragment extends Fragment {

    private Button btn_add;
    private EditText tv_new_Status;

    private RecyclerView rvResults;
    private BookViewModel vm;
    private StatusAdapter adapter;

    public StatusesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statuses, container, false);

        rvResults = view.findViewById(R.id.lv_statuses);
        vm = new ViewModelProvider(this).get(BookViewModel.class);

        adapter = new StatusAdapter(this::onItemLongClick);
        rvResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        rvResults.setAdapter(adapter);

        btn_add = view.findViewById(R.id.btn_add);
        tv_new_Status = view.findViewById(R.id.t_newStatus);

        btn_add.setOnClickListener(v->{
            if(tv_new_Status.getText().toString().isEmpty()){
                return;
            }
            vm.addStatus(tv_new_Status.getText().toString());
            tv_new_Status.setText("");
        });

        vm.getAllStatuses().observe(getViewLifecycleOwner(), books -> {
            adapter.setItems(books);
        });

        return view;
    }

    private void onItemLongClick(Status status, View anchor) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        Menu menu = pm.getMenu();
        menu.add("Удалить статус").setOnMenuItemClickListener(i -> {
            vm.deleteStatus(status);
            return true;
        });

        pm.show();
    }
}