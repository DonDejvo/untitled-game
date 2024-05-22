package com.webler.goliath.eventsystem;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.events.Event;
import com.webler.goliath.eventsystem.listeners.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class EventManager {
    private static final Logger logger = Logger.getLogger(EventManager.class.getName());
    private static final HashMap<Class<? extends Event>, CopyOnWriteArrayList<Listener>> registeredListeners =
            new HashMap<>();

    /**
    * Registers all event handlers in the given class instance. This method is called by Jitsi and should not be called directly
    * 
    * @param listenerClassInstance - class instance to scan for
    */
    public static void registerListeners(Component listenerClassInstance) {
        for (Method method : listenerClassInstance.getClass().getMethods()) {
            // If the annotation is present in the event handler method.
            if(!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            // This method is used to check if the number of arguments is 1.
            if(method.getParameterTypes().length != 1) {
                logger.info("Ignoring illegal event handler: " + method.getName() +
                        ": Wrong number of arguments (required: 1)");
                continue;
            }

            // Check if the event handler is assignable to the event handler.
            if(!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                logger.info("Ignoring illegal event handler: " + method.getName() + ": Argument must extend " +
                        Event.class.getName());
                continue;
            }

            @SuppressWarnings("unchecked") Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            Listener listener = new Listener(listenerClassInstance, method);
            addListener(eventClass, listener);
        }
    }

    /**
    * Removes all CopyOnWriteListeners registered to the given class instance. This is useful for unit tests that need to know when an instance of a listener is no longer needed.
    * 
    * @param listenerClassInstance - the class instance that should no longer be
    */
    public static void unregisterListeners(Component listenerClassInstance) {
        for (CopyOnWriteArrayList<Listener> listenerList : registeredListeners.values()) {
            // Removes all listeners from the list of listeners that have been registered with this class instance.
            for (int i = 0; i < listenerList.size(); ++i) {
                // Removes the listener from the listenerList.
                if (listenerList.get(i).listenerClassInstance == listenerClassInstance) {
                    listenerList.remove(i);
                    i -= 1;
                }
            }
        }
    }

    /**
    * Adds a listener to the list of listeners for the specified event. It is assumed that the listener will be called in the context of the thread that created the event
    * 
    * @param eventClass - the class of the event
    * @param listener - the listener to be added to the list of
    */
    private static void addListener(Class<? extends Event> eventClass, Listener listener) {
        // Register a listener for the given event class.
        if(!registeredListeners.containsKey(eventClass)) {
            registeredListeners.put(eventClass, new CopyOnWriteArrayList<>());
        }

        registeredListeners.get(eventClass).add(listener);
    }

    /**
    * Dispatches an event to all registered listeners. This method is called by EventDispatcher. dispatchEvent to be notified of event arrival
    * 
    * @param event - the event to be
    */
    public static void dispatchEvent(Event event) {
        CopyOnWriteArrayList<Listener> listeners = registeredListeners.get(event.getClass());
        // Dispatches event to all listeners.
        if(listeners != null) {
            for(Listener listener : listeners) {
                listener.listenerMethod.setAccessible(true);
                try {
                    listener.listenerMethod.invoke(listener.listenerClassInstance, event);
                } catch (IllegalAccessException e) {
                    logger.warning("Could not access event handler method:");
                } catch (InvocationTargetException e) {
                    logger.warning("Could not dispatch event to handler " + listener.listenerMethod.getName());
                }
            }
        }
    }

    private record Listener(Object listenerClassInstance, Method listenerMethod) {
    }
}
