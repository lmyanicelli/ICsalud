package com.luciayanicelli.icsalud.Activity_Autodiagnostico;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//Asigna a cada Tab del Autodiagnóstico el fragment correspondiente

public class PagerAdapter extends FragmentStatePagerAdapter {
     private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Autodiagnostico_Peso tab1 = new Autodiagnostico_Peso();
                return new Autodiagnostico_Peso();
            case 1:
                Autodiagnostico_PAFC tab2 = new Autodiagnostico_PAFC();
                return tab2;
            case 2:
                Autodiagnostico_Sintomas tab3 = new Autodiagnostico_Sintomas();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    @Override
    public int getItemPosition(Object object) {
        Fragment f = (Fragment) object;
      /*  if (f != null) {
           // f.update();
        }
        */
        return super.getItemPosition(object);
    }




}
