package org.firez.bettercoalcarts;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
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
    private ArrayList<LinkedCar> trainCars = new ArrayList<LinkedCar>();


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
        //If this is a furnace cart then do speed operations if powered
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
                        ((PoweredMinecart) vehicle).setMaxSpeed(0.68D);
                        vehicle.teleport(vehicle.getLocation().add(velocity.multiply(1f)));
                        vehicle.getLocation().add(velocity.multiply(20));
                        int cartNum = 0;
                        int maxCars = 5;
                        ArrayList<Minecart> attached = new ArrayList<>();
                        for(Minecart c: carts){
                            if(c.getType()==EntityType.MINECART_FURNACE||furnaceCart.equals(c)){
                                continue;
                            }
                            for(int i = 0; i<=maxCars*2;i++){
                                Location vecLoc = vehicle.getLocation();

                                Location loc = vecLoc.add(vehicle.getFacing().getOppositeFace().getDirection().multiply(i));
                                if(c.getLocation().distance(loc)<=3f){
                                    attached.add(c);
                                    break;
                                }
                            }
                        }
                        Bukkit.getLogger().info("Attached:"+attached.size());
                        for(Minecart c:attached){
                            if(!furnaceCart.equals(c) && c.getType() != EntityType.MINECART_FURNACE) {
                                cartNum++;
                                Location teleportLoc = vehicle.getLocation().add(vehicle.getFacing().getOppositeFace().getDirection().multiply(2*cartNum));
                                if(c.getPassengers().size()>0){
                                    Entity e = c.getPassengers().get(0);
                                    e.teleport(teleportLoc);
                                    c.teleport(teleportLoc);
                                    c.addPassenger(e);
                                    c.setMaxSpeed(4f);
                                    c.setVelocity(vehicle.getVelocity());

                                }else{
                                    c.teleport(teleportLoc);
                                    c.setMaxSpeed(4f);
                                    c.setVelocity(vehicle.getVelocity());
                                }


                            }
                        }
                    }

                }
            }
        }
        //If this is not a furnace cart then do attachment operations.
        else if(vehicle.getType()==EntityType.MINECART&&false){
            Minecart cart = (Minecart) vehicle;
            if(!inCartList((Minecart)vehicle)){
                this.carts.add((Minecart) vehicle);
            }
            for(Minecart c:carts){
                if(!cart.equals(c) && c.getType() == EntityType.MINECART_FURNACE&&c.getVelocity().length()>0){
                    Location c_loc = c.getLocation();
                    Location _for = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getDirection());
                    Location _back = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getOppositeFace().getDirection());
                    double forDist = _for.distance(cart.getLocation());
                    double backDist = _back.distance(cart.getLocation());
                    Vector normalizedVelocity  = c.getVelocity().normalize();
                    if(forDist<=3.0||backDist<=3.0){
                        cart.teleport(c.getLocation().add(c.getFacing().getOppositeFace().getDirection()));
                        Bukkit.getLogger().info("Velocity:"+c.getVelocity().toString());
                        if(c.getVelocity().length()>0.3D){
                            cart.teleport(c.getLocation());
                        }
                    }
                    if(c.getVelocity().length()>0.3D){
                        cart.teleport(c.getLocation().add(c.getFacing().getDirection().multiply(5)));
                    }

                }
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
        if(vehicle.getType()==EntityType.MINECART&&vehicle.getType()!=EntityType.MINECART_FURNACE&&false){
            Minecart cart = (Minecart) vehicle;
            if(!inCartList((Minecart)vehicle)){
                this.carts.add((Minecart) vehicle);
            }
            for(Minecart c:carts){
                if(!cart.equals(c) && c.getType() == EntityType.MINECART_FURNACE&&c.getVelocity().length()>0){
                    Location c_loc = c.getLocation();
                    Location _for = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getDirection());
                    Location _back = addVecToLoc(c.getWorld(),c_loc,c.getFacing().getOppositeFace().getDirection());
                    double forDist = _for.distance(cart.getLocation());
                    double backDist = _back.distance(cart.getLocation());
                    Vector normalizedVelocity  = c.getVelocity().normalize();


                    if(forDist<=3.0||backDist<=6.0){
                        cart.teleport(c.getLocation().add(c.getFacing().getOppositeFace().getDirection()));
                        Bukkit.getLogger().info("Velocity:"+c.getVelocity().toString());
                        if(c.getVelocity().length()>0.1D){
                            cart.teleport(c.getLocation());
                        }
                    }
                    if(c.getVelocity().length()>0.3D){
                        cart.teleport(c.getLocation().add(c.getFacing().getDirection().multiply(5)));
                    }


                }
            }
        }
    }
}
