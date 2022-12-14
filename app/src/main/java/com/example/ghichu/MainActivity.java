package com.example.ghichu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ghichu.adapters.NotesListAdapter;
import com.example.ghichu.api.ApiService;
import com.example.ghichu.components.DrawingActivity;
import com.example.ghichu.components.Identify;
import com.example.ghichu.components.NotesTakerActivity;
import com.example.ghichu.components.ReminderFragment;
import com.example.ghichu.components.ReminderViews;
import com.example.ghichu.models.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private List<NoteModel> notes;
    private SearchView searchView_home;
    private NoteModel selectedNote;
    private LottieAnimationView searchView_loader,search_load;
    private Timer timer;
    private TextView textView_select,textView_takeaphoto;

    FloatingActionButton fab_add;
    NotesListAdapter notesListAdapter;
    LinearLayout noteEmpty;
    RelativeLayout wrapper;

    //sidebar
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CardView cardView;
    Toolbar toolbar;
    Switch switchBg;
    public static boolean isChecked = false;
    //    View fragment_container;

    Button view_list;

    private static Boolean toggle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wrapper = findViewById(R.id.wrapper);
        drawerLayout = findViewById(R.id.drawerlayout);
        searchView_loader = findViewById(R.id.searchView_loader);

        recyclerView = findViewById(R.id.recycle_home);
        noteEmpty = findViewById(R.id.note_empty);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);
        search_load = findViewById(R.id.search_load);
        cardView=findViewById(R.id.cardView);

        textView_select=findViewById(R.id.textView_select);
        textView_takeaphoto=findViewById(R.id.textView_takeaphoto);

//        fragment_container = findViewById(R.id.fragment_container);

        //sidebar
        toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);

        //click sidebar
        navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront(); //binding
        navigationView.setNavigationItemSelectedListener(this);

        //siderbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_open,
                R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //DEFAULT SIDEBAR AUTO SELECT NOTE
        navigationView.setCheckedItem(R.id.note_menu);


        //toggle view
        view_list=findViewById(R.id.view_list);
        notes = new ArrayList<>();

        updateRecycler(2);
        if(notes.size()==0){
            noteEmpty.setVisibility(View.VISIBLE);
        }

        //add note
        fab_add.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("switchBg",String.valueOf(switchBg));
            startActivityIfNeeded(intent,101); //WRITE_PERMISSION
        });

        //Search note
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return true;
            }

            //services
            private void filter(String s) {
                List<NoteModel> filteredList = new ArrayList<>();
                for(NoteModel singleNote:notes){
                    if(singleNote.getTitle().toLowerCase().contains(s.toLowerCase())||singleNote.getNotes().toLowerCase().contains(s.toLowerCase())){
                        filteredList.add(singleNote);
                    }
                }
                notesListAdapter.filteredList(filteredList);
            }
        });

        if(cardView.isFocused()){
            cardView.setVisibility(View.INVISIBLE);
        }
        
        //take a photo
        textView_takeaphoto.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,CameraPicture.class);
            startActivity(i);
        });
        
        //select image
        textView_select.setOnClickListener(view->{
            Intent i = new Intent(MainActivity.this,NotesTakerActivity.class);
            startActivity(i);
        });

        //switch bg
        switchBg =(Switch) findViewById(R.id.switchBg);
        switchBg.setOnClickListener(view->{
            isChecked = switchBg.isChecked();
            if(isChecked){
                wrapper.setBackgroundColor(getResources().getColor(R.color.black));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.black));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }else{
                wrapper.setBackgroundColor(getResources().getColor(R.color.white));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.white));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            }
        });
    }

    //Button
    public void drawing(View view){
        Intent clickDraw = new Intent(MainActivity.this, DrawingActivity.class);
        startActivity(clickDraw);
    }

    public void picture(View view){
        int visible=cardView.getVisibility();
        if(visible==4)
            cardView.setVisibility(View.VISIBLE);
        else
            cardView.setVisibility(View.INVISIBLE);
    }



    //toggle view
    public void toggleView(View view){
        if(toggle) {
            view_list.setBackgroundResource(R.drawable.verticals);
            updateRecycler(1);
            toggle=false;
        }else{
            view_list.setBackgroundResource(R.drawable.grid);
            updateRecycler(2);
            toggle=true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            //WRITE_PERMISSION
            if(requestCode==101){
                if(resultCode== Activity.RESULT_OK){

                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();

                    NoteModel new_note = (NoteModel) (gson.fromJson((String) data.getSerializableExtra("newNote"),NoteModel.class));
                    notes.add(new_note);
                    Toast.makeText(MainActivity.this,"Add Note Success",Toast.LENGTH_LONG).show();

                    notesListAdapter.notifyDataSetChanged();
                }
            }else if(requestCode==102){
                if(resultCode== Activity.RESULT_OK){
                    NoteModel new_note = (NoteModel) data.getSerializableExtra("newNote");
                    ApiService.apiService.updateNote(new_note).enqueue(new Callback<NoteModel>() {
                        @Override
                        public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                            notes.clear();
                            ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
                                @Override
                                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                                    notes.addAll(response.body());
                                    Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_LONG).show();
                                    notesListAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<NoteModel> call, Throwable t) {
                            Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                        }
                    });
                    notesListAdapter.notifyDataSetChanged();
                }
            }
    }

    //Grid:split two column
    private void updateRecycler(int numberColumn) {
        ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
            @Override
            public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                notes = response.body();
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberColumn, LinearLayoutManager.VERTICAL));

                if(notes.size()>0){
                    noteEmpty.setVisibility(View.INVISIBLE);
                }
                notesListAdapter = new NotesListAdapter(MainActivity.this,notes,notesClickListener);
                recyclerView.setAdapter(notesListAdapter);
            }
            @Override
            public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                Log.e("getAllNotes",t.getMessage());
                Toast.makeText(MainActivity.this,"Error Get All Notes",Toast.LENGTH_LONG).show();
            }
        });

    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(NoteModel noteModal) {
            Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
            intent.putExtra("noteOld", noteModal.getId());
                startActivityIfNeeded(intent,102); //EDITING
        }

        @Override
        public void onLongClick(NoteModel notes, CardView cardView) {
            selectedNote = notes;
            showPopup(cardView);
        }

        private void showPopup(CardView cardView) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this,cardView);
            popupMenu.setOnMenuItemClickListener(MainActivity.this);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.show();
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pin:
                    ApiService.apiService.updatePinned(selectedNote.getId()).enqueue(new Callback<NoteModel>() {
                        @Override
                        public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                            notes.clear();
                            ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
                                @Override
                                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                                    notes.addAll(response.body());
                                    Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_LONG).show();
                                    notesListAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<NoteModel> call, Throwable t) {
                            Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                        }
                    });
                notesListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete:
                notes.remove(selectedNote);
                ApiService.apiService.deleteNote(selectedNote.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(MainActivity.this,"Delete Successful",Toast.LENGTH_LONG).show();
                        notesListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Delete Fail",Toast.LENGTH_LONG).show();
                    }
                });
                notesListAdapter.notifyDataSetChanged();
                return true;
            case R.id.clone:
                ApiService.apiService.addNote(selectedNote).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        Toast.makeText(MainActivity.this,"Clone success",Toast.LENGTH_LONG).show();
                        notes.add(selectedNote);
                        notesListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Clone Fail",Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //animation search loader
        searchView_loader.animate().translationX(-2600).setDuration(800).setStartDelay(1800);
        search_load.animate().scaleY(0).setDuration(500).setStartDelay(1800);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                searchView_home.setQueryHint("Search notes...");
            }
        },3000);
    }


    //handle click sidebar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.note_menu:
//                fragment_container.setVisibility(View.INVISIBLE);
//                recyclerView.setVisibility(View.VISIBLE);
                navigationView.setCheckedItem(R.id.note_menu);
                break;
            case R.id.reminder_menu:
                Intent i =new Intent(MainActivity.this, ReminderViews.class);
                startActivity(i);
//                fragment_container.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.INVISIBLE);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ReminderFragment()).commit();
                break;
            case R.id.login_menu:
                Intent login =new Intent(MainActivity.this, Identify.class);
                startActivity(login);
                break;
            }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}