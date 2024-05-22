package com.webler.goliath.graphics;

public record Uniform(String name, int location, String type) {

    /**
    * Returns true if this uniform is equal to the specified uniform. This is used to determine whether or not a uniform should be drawn on the graph based on its location
    * 
    * @param uniform - the uniform to compare with
    * 
    * @return true if the uniform is equal to the specified uniform false otherwise ( not equal in this case ) Note : this method does not compare uniform's
    */
    public boolean equals(Uniform uniform) {
        return location == uniform.location;
    }

    /**
    * Compares this object with another object. This is used to determine if two objects are equal or not.
    * 
    * @param obj - the object to compare to. May be null.
    * 
    * @return true if the objects are equal false otherwise. Note that false is always returned for objects that are not equal
    */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
    * Returns the hash code for this object. This is used to compare two objects by checking if they are equal.
    * 
    * 
    * @return the hash code for this object or Integer. MAX_VALUE if there is no hash code for this
    */
    @Override
    public int hashCode() {
        return location;
    }
}

