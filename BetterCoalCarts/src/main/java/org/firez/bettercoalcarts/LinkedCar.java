package org.firez.bettercoalcarts;

import org.bukkit.entity.Minecart;

public interface LinkedCar {
    public Boolean isLocomotive();

    public LinkedCar getNext();

    public LinkedCar getPrevious();
    public void SetNext(LinkedCar next);
    public void SetPrev(LinkedCar prev);
    public Minecart getCurrent();
}
