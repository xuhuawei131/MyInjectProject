package con.lingdian.myinjectproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lingdian.injectapi.ViewInjector;
import com.xuhuawei.myannotation.ViewInject;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.text_test)
    TextView text_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);

        text_test.setText("I'm succesfull");
    }
}
