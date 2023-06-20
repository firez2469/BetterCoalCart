package org.firez.bettercoalcarts;

import org.bukkit.entity.Minecart;

public class TrainCar implements LinkedCar {

    private LinkedCar next;
    private  LinkedCar prev;
    private Minecart current;
    public TrainCar(Minecart current){
        this.current=current;
    }

    @Override
    public Boolean isLocomotive() {
        return false;
    }

    @Override
    public LinkedCar getNext() {
        return this.next;
    }

    @Override
    public LinkedCar getPrevious() {
        return this.prev;
    }

    @Override
    public void SetNext(LinkedCar next) {
        this.next = next;
    }

    @Override
    public void SetPrev(LinkedCar prev) {
        this.prev = prev;
    }

    @Override
    public Minecart getCurrent() {
        return current;
    }

}
