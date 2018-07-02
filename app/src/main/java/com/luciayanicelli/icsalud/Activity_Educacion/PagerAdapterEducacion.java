package com.luciayanicelli.icsalud.Activity_Educacion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//Asigna a cada Tab del Autodiagnóstico el fragment correspondiente

public class PagerAdapterEducacion extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapterEducacion(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PreguntasFrecuentes_Educacion tab1 = new PreguntasFrecuentes_Educacion();
                return tab1;
         /*   case 1:
                WikiCardio tab2 = new WikiCardio();
                return tab2;
       */
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
        /*if (f != null) {
           // f.update();
        }
        */
        return super.getItemPosition(object);
    }




}
