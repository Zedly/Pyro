/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import org.bukkit.event.Listener;

/**
 *
 * @author Dennis
 */
public abstract class FeatureClass implements Listener, Runnable {

    public int getTaskFrequency() {
        return 1000;
    }

    @Override
    public void run() {
    }

}
