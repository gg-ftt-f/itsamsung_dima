package com.example.it_samsung_project_v1.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.vm.BookViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenresListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenresListFragment extends Fragment {



    private BookViewModel bookViewModel;

    private CheckBox ch_fantasy;
    private CheckBox ch_science;
    private CheckBox ch_romance;
    private CheckBox ch_mystery;
    private CheckBox ch_thriller;
    private CheckBox ch_horror;
    private Spinner spinner;

    private Button btn;

    String[] lng = {"Русский", "Английский" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_genres_list, container, false);

        spinner = view.findViewById(R.id.sp_lang);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, lng);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);



        btn = view.findViewById(R.id.btn_statuses);
        btn.setOnClickListener(v->{
            var statusesFragment = new StatusesFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, statusesFragment)
                    .commit();
        });

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)parent.getItemAtPosition(position);
                BookViewModel.isEn = (item.equals("Английский"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);


        bookViewModel = new ViewModelProvider(requireActivity())
                .get(BookViewModel.class);
        ch_fantasy = view.findViewById(R.id.ch_fantasy);
        ch_fantasy.setChecked(BookViewModel.gn_fantasy);
        ch_fantasy.setOnClickListener(v->{
            BookViewModel.gn_fantasy = ch_fantasy.isChecked();
        });

        ch_science = view.findViewById(R.id.ch_science);
        ch_science.setChecked(BookViewModel.gn_science);
        ch_science.setOnClickListener(v-> {
                    BookViewModel.gn_science = ch_science.isChecked();
                }) ;


        ch_romance = view.findViewById(R.id.ch_romance);
        ch_romance.setChecked(BookViewModel.gn_romance);
        ch_romance.setOnClickListener(v-> {
            BookViewModel.gn_romance = ch_romance.isChecked();
        }) ;


        ch_mystery = view.findViewById(R.id.ch_mystery);
        ch_mystery.setChecked(BookViewModel.gn_mystery);
        ch_mystery.setOnClickListener(v-> {
            BookViewModel.gn_mystery = ch_mystery.isChecked();
        }) ;

        ch_thriller = view.findViewById(R.id.ch_thriller);
        ch_thriller.setChecked(BookViewModel.gn_thriller);
        ch_thriller.setOnClickListener(v-> {
            BookViewModel.gn_thriller = ch_thriller.isChecked();
        }) ;

        ch_horror = view.findViewById(R.id.ch_horror);
        ch_horror.setChecked(BookViewModel.gn_horror);
        ch_horror.setOnClickListener(v-> {
            BookViewModel.gn_horror = ch_horror.isChecked();
        }) ;
        return view;
    }
}