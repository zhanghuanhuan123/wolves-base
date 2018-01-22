 package com.wolves.wolf.base.db;

 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;

 public class SimplePage
   implements Serializable
 {
   private int pages = -1;
   private int currentPage = -1;
   private int totalSize = -1;
   private int currentPageSize = -1;
   private int recordsPerPage = -1;
   private List results = new ArrayList();
   private List meta1 = new ArrayList();

   public void initialSize(int size, int recordsPerPage, int currentPage) {
     this.totalSize = size;
     this.pages = ((size - 1) / recordsPerPage + 1);
     this.pages = (this.pages < 0 ? 0 : this.pages);
     this.recordsPerPage = recordsPerPage;
     this.currentPage = currentPage;
   }

   public int getPages()
   {
     return this.pages;
   }

   public void setPages(int pages) {
     this.pages = pages;
   }

   public int getCurrentPage()
   {
     return this.currentPage;
   }

   public void setCurrentPage(int currentPage) {
     this.currentPage = currentPage;
   }

   public int getTotalSize()
   {
     return this.totalSize;
   }

   public void setTotalSize(int totalSize) {
     this.totalSize = totalSize;
   }

   public int getCurrentPageSize()
   {
     return this.currentPageSize;
   }

   public void setCurrentPageSize(int currentPageSize) {
     this.currentPageSize = currentPageSize;
   }

   public int getRecordsPerPage()
   {
     return this.recordsPerPage;
   }

   public void setRecordsPerPage(int recordsPerPage) {
     this.recordsPerPage = recordsPerPage;
   }

   public List getResults()
   {
     return this.results;
   }

   public void setResults(List results) {
     this.results = results;
   }

   public List getMeta1()
   {
     return this.meta1;
   }

   public void setMeta1(List meta1) {
     this.meta1 = meta1;
   }

   public String toString() {
     return "SimplePage{pages=" + this.pages + ", currentPage=" + this.currentPage + ", totalSize=" + this.totalSize + ", currentPageSize=" + this.currentPageSize + ", recordsPerPage=" + this.recordsPerPage + ", results=" + this.results + ", meta1=" + this.meta1 + '}';
   }
 }
