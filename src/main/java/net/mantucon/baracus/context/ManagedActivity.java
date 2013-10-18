package net.mantucon.baracus.context;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 23.09.13
 * Time: 08:34
 * To change this template use File | Settings | File Templates.
 */
public class ManagedActivity extends Activity{

    protected View underlyingView;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.underlyingView = getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    protected void onDestroy() {
        BaracusApplicationContext.unregisterErrorhandlersForView(underlyingView);
        super.onDestroy();
    }

}
