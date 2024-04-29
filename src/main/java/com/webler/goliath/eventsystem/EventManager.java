package com.webler.goliath.eventsystem;

import com.webler.goliath.eventsystem.events.Event;
import com.webler.goliath.eventsystem.listeners.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static final HashMap<Class<? extends Event>, CopyOnWriteArrayList<Listener>> registeredListeners =
            new HashMap<>();

    public static void registerListeners(Object listenerClassInstance) {
        for (Method method : listenerClassInstance.getClass().getMethods()) {
            if(!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            if(method.getParameterTypes().length != 1) {
                System.err.println("Ignoring illegal event handler: " + method.getName() +
                        ": Wrong number of arguments (required: 1)");
                continue;
            }

            if(!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                System.err.println("Ignoring illegal event handler: " + method.getName() + ": Argument must extend " +
                        Event.class.getName());
                continue;
            }

            @SuppressWarnings("unchecked") Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            Listener listener = new Listener(listenerClassInstance, method);
            addListener(eventClass, listener);
        }
    }

    public static void unregisterListeners(Object listenerClassInstance) {
        for (CopyOnWriteArrayList<Listener> listenerList : registeredListeners.values()) {
            for (int i = 0; i < listenerList.size(); ++i) {
                if (listenerList.get(i).listenerClassInstance == listenerClassInstance) {
                    listenerList.remove(i);
                    i -= 1;
                }
            }
        }
    }

    private static void addListener(Class<? extends Event> eventClass, Listener listener) {
        if(!registeredListeners.containsKey(eventClass)) {
            registeredListeners.put(eventClass, new CopyOnWriteArrayList<>());
        }

        registeredListeners.get(eventClass).add(listener);
    }

    public static void dispatchEvent(Event event) {
        CopyOnWriteArrayList<Listener> listeners = registeredListeners.get(event.getClass());
        if(listeners != null) {
            for(Listener listener : listeners) {
                listener.listenerMethod.setAccessible(true);
                try {
                    listener.listenerMethod.invoke(listener.listenerClassInstance, event);
                } catch (IllegalAccessException e) {
                    System.err.print("Could not access event handler method:");
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Could not dispatch event to handler " +
                            listener.listenerMethod.getName(), e);
                }
            }
        }
    }

    private record Listener(Object listenerClassInstance, Method listenerMethod) {
    }
}
