package allymahmoud.com.facelessmanapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Allymahmoud on 10/11/17.
 */

public class PlayFragment extends Fragment {
    private View view;
    private Button playbutton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.play_fragment, container, false);
        playbutton = (Button) view.findViewById(R.id.buttonplay);

        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "PLAY TAB COMING SOON...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }
}
