package org.firez.bettercoalcarts;

import org.bukkit.Location;
import org.bukkit.entity.Minecart;

public class Locomotive implements  LinkedCar{
    private LinkedCar next;
    private Minecart current;
    public Locomotive(Minecart current){
        this.current=current;
    }

    @Override
    public Boolean isLocomotive() {
        return true;
    }

    @Override
    public LinkedCar getNext() {
        return next;
    }

    @Override
    public LinkedCar getPrevious() {
        return null;
    }

    @Override
    public void SetNext(LinkedCar next) {
        this.next = next;
    }

    @Override
    public void SetPrev(LinkedCar prev) {
        //Nothing
    }

    @Override
    public Minecart getCurrent() {
        return current;
    }
}
