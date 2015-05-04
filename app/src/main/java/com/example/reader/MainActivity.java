package com.example.reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Criando uma array falsa de strings com os títulos das nossas Aulas
            String[] data = {
                    "Aula 1: Getting Start!",
                    "Aula 2: Como instalar o Android Studio?",
                    "Aula 3: Como criar um projeto no Android Studio?",
                    "Aula 4: O que é Activity e Fragment?",
                    "Aula 5: Como criar a User Interface (UI) do meu Aplicativo Android?"
            };

            // Criando uma lista (ArrayList) com os dados criados acima
            List<String> listOfLastPosts = new ArrayList<String>(Arrays.asList(data));

            // Agora que já temos os dados, vamos criar um Adapter, no caso um ArrayAdapter

            ArrayAdapter<String> listOfLastPostsAdapter = new ArrayAdapter<String>(
                    getActivity(), // O contexto atual
                    R.layout.list_item_last_posts, // O arquivo de layout de cada item
                    R.id.list_item_post_title_textview, // O ID do campo a ser preenchido
                    listOfLastPosts // A fonte dos dados
            );

            // Inflamos o layout principal
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Cria uma referência para a ListView
            ListView listView = (ListView) rootView.findViewById(R.id.list_last_posts);
            listView.setAdapter(listOfLastPostsAdapter);

            // Retornamos tudo
            return rootView;
        }
    }
}
