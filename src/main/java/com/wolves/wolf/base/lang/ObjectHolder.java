 package com.wolves.wolf.base.lang;

 public class ObjectHolder<E>
 {
   private E value;

   public void set(E value)
   {
     this.value = value;
   }

   public E get() {
     return this.value;
   }
 }

