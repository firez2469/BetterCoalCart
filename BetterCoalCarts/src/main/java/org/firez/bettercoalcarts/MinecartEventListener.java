package org.firez.bettercoalcarts;

import org.bukkit.*;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.EventListener;

public class MinecartEventListener implements Listener {
    private ArrayList<Minecart> carts = new ArrayList<>();
    private int iteration = 0;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getLogger().info(ChatColor.GREEN+"Tracking "+event.getPlayer().getName()+"!");
    }
    private Boolean isTurn(Location l){
        if(l.getBlock().getType()==Material.RAIL){
            Rail r = (Rail)(l.getBlock().getBlockData());
            return r.getShape()!= Rail.Shape.EAST_WEST&&r.getShape()!=Rail.Shape.NORTH_SOUTH;
        }
        return false;
    }
    private Boolean isRail(Location l){
        return l.getBlock().getType()==Material.RAIL;
    }
    private Boolean isCautious(Location location){
        if(!isRail(location)){
            return true;
        }
        Boolean caution = false;
        for(double x=-1;x<=1;x++){
            for(double z =-1;z<=1;z++){
                Location l = new Location(location.getWorld(),location.getX()+x,location.getY(),location.getZ()+z);
                if(isRail(l)&&isTurn(l)){
                    caution=true;
                }
            }
        }
        return caution;
    }

    private Boolean isAttachedToFurnace(Location location){

        return false;
    }
    private Boolean isCart(Vehicle vehicle){
        return vehicle.getType()==EntityType.MINECART||
                vehicle.getType()==EntityType.MINECART_CHEST||
                vehicle.getType()==EntityType.MINECART_FURNACE||
                vehicle.getType()==EntityType.MINECART_HOPPER||
                vehicle.getType()==EntityType.MINECART_TNT;
    }
    private Boolean inCartList(Minecart cart){
        for(Minecart c:carts){
            if(c.equals(cart)){
                return true;
            }
        }
        return false;
    }
    private Location addVecToLoc(World w, Location loc, Vector v){
        Location l = new Location(w,loc.getX()+v.getX(),loc.getY()+v.getY(),loc.getZ()+v.getZ());
        return l;
    }
    private Location subVecToLoc(World w, Location loc, Vector v){
        Location l = new Location(w,loc.getX()-v.getX(),loc.getY()-v.getY(),loc.getZ()-v.getZ());
        return l;
    }
    @EventHandler
    public void onVehicleSpawn(VehicleCreateEvent event){
        Vehicle vehicle = event.getVehicle();
        if(isCart(vehicle)){
            this.carts.add((Minecart) vehicle);
        }
    }


    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event){

        Vehicle vehicle = event.getVehicle();
        Vector velocity = vehicle.getVelocity();
        Double mag = velocity.length();
        if(vehicle.getType()== EntityType.MINECART_FURNACE){
            if(!inCartList((Minecart)vehicle)){
                this.carts.add((Minecart) vehicle);
            }
            if(event.getTo().getBlock().getType()== Material.RAIL){
                Rail r = (Rail)(event.getTo().getBlock().getBlockData());
                Boolean isChange = r.getShape()!= Rail.Shape.EAST_WEST&&r.getShape()!=Rail.Shape.NORTH_SOUTH;
                if(isCautious(event.getTo())){
                    return;
                }
                if(!isChange){
                    PoweredMinecart furnaceCart = (PoweredMinecart) vehicle;
                    if(furnaceCart.getFuel()>0){
                        Bukkit.getLogger().info(event.getVehicle().getName()+" speed="+mag.toString());
                        ((PoweredMinecart) vehicle).setMaxSpeed(0.68D);
                        vehicle.teleport(vehicle.getLocation().add(velocity));
                        //furnaceCart.setVelocity(furnaceCart.getVelocity().multiply(5f));
                        Bukkit.getLogger().info("PUSHING!");
                    }
                }
            }
        }
        else if(vehicle.getType()==EntityType.MINECART){
            Minecart cart = (Minecart) vehicle;
            if(!inCartList((Minecart)vehicle)){
                this.carts.add((Minecart) vehicle);
            }
            for(Minecart c:carts){
                /*if(!cart.equals(c) && c.getType() == EntityType.MINECART_FURNACE){
                    Location c_loc = c.getLocation();
                    Location _for = addVecToLoc(c.getWorld(),c_loc,c.getVelocity().normalize());
                    Location _back = subVecToLoc(c.getWorld(),c_loc,c.getVelocity().normalize());

                    if(_for.distance(cart.getLocation()) <= 3.0){
                        cart.teleport(_for.add(0, 0, 3.0)); // position the minecart 3 units in front of the furnace minecart
                    }
                    else if(_back.distance(cart.getLocation()) <= 3.0){
                        cart.teleport(_back.subtract(0, 0, 3.0)); // position the minecart 3 units behind the furnace minecart
                    }
                }*/
            }
        }
    }
    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event){
        Vehicle vehicle = event.getVehicle();
        if(vehicle.getType()==EntityType.MINECART||vehicle.getType()==EntityType.MINECART_FURNACE) {
            Minecart cart = (Minecart) vehicle;
            if (inCartList((Minecart) vehicle)) {
                Bukkit.getLogger().info("REMOVED VEHICLE!");
                this.carts.remove((Minecart) vehicle);
            }
        }
    }
    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event){
        Vehicle vehicle = event.getVehicle();
        if(vehicle.getType()==EntityType.MINECART&&vehicle.getType()!=EntityType.MINECART_FURNACE){
            Minecart cart = (Minecart) vehicle;
            if(!inCartList((Minecart)vehicle)){
                this.carts.add((Minecart) vehicle);
            }
            for(Minecart c:carts){
                if(!cart.equals(c) && c.getType() == EntityType.MINECART_FURNACE){
                    Location c_loc = c.getLocation();
                    Location _for = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getDirection());
                    Location _back = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getOppositeFace().getDirection());
                    double forDist = _for.distance(cart.getLocation());
                    double backDist = _back.distance(cart.getLocation());

                    if(forDist <= 3.0){
                        //cart.teleport(_for);
                        cart.teleport(_for.add(c.getVelocity().multiply(2)));
                        //cart.teleport(_for.add(0, 0, 3)); // position the minecart 3 units in front of the furnace minecart
                    }
                    else if(backDist<=3.0){
                        cart.teleport(_back.add(c.getVelocity().multiply(2)));
                        //cart.teleport(_back.add(0, 0, 3)); // position the minecart 3 units behind the furnace minecart
                    }
                }
            }
        }
    }
}
