package prog12;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import prog06.ArrayQueue;
import prog07.SkipList;

import prog10.BTree;

public class MyGoogle implements Google {
	
	
	private List<String> allURLs;
	//List of all urls
	
	private Map<String, Integer> url2index;
	//(It should take a URL and give us an index

	private List<Integer> refCounts;
		// keep track of the significance of each web page, which
	//for simplicity is the number of references to it from other web pages.
	 
	
	private List<String> allWords;
	//keep track of all the words we see and index them too

	private Map<String, Integer> word2index;
	//look up the index of a word
	
	private List< List<Integer> > urlIndexLists;
	//for each word, we need to know the list of web pages which
	//have that word. 
	
	
	
	public MyGoogle(){
		
	  allURLs = new ArrayList<String>();
	  url2index= new TreeMap<String,Integer>();
	  refCounts= new ArrayList<Integer>();
	  allWords= new ArrayList<String>();
	  word2index= new HashMap<String,Integer>();
	  urlIndexLists= new ArrayList<List<Integer>>();
	}

	
	@Override
	public void gather(Browser browser, List<String> startingURLs) {
		// TODO Auto-generated method stub
	  Queue <String> urlQueue= new ArrayQueue<String>();
		//int index= url2index.get(urlQueue.poll());
		//For each one,
		//check to make sure we haven't seen it before (**how?).  If not, index
		//it and put it in a Queue.
	  for(int x=0; x< startingURLs.size(); x++){
	    if(!url2index.containsKey(startingURLs.get(x))){
		  indexUrl(startingURLs.get(x));
		  urlQueue.offer(startingURLs.get(x));			
	    }
	  }
		
//.For each
//		new URL, index it and put it in the queue.  Index each new word.  Add
//		the index of the current page (the one we just dequeued) to the list
//		for each word.  Increment the reference count for every page that
//		referenced.
	
      int count = 0;
		//While the queue is not empty, take out a URl
      while (!urlQueue.isEmpty() && count++ < 100) {
		  	String url = urlQueue.poll();
		  	Set<String> URLset= new HashSet<String>();
		  	Set<String>wordSet= new HashSet<String>();
		  	List<String> wordList= new ArrayList<String>();
//			Using the browser i	provide you, get the List of words and URLs on that page.
		  	if(browser.loadPage(url)){
		  	  for (int n=0; n < browser.getURLs().size(); n++){
		  	    if(!url2index.containsKey(browser.getURLs().get(n))){
		  		  indexUrl(browser.getURLs().get(n));
		  		  urlQueue.offer(browser.getURLs().get(n));
		  		} 
		  		if(URLset.add(browser.getURLs().get(n))){
		  		  int urlIndex= url2index.get(browser.getURLs().get(n));
		  		  refCounts.set(urlIndex, refCounts.get(urlIndex)+1);
		  	  	}
		  	  }
		  		
		  	}	
		  	List<String> browserWords= browser.getWords();
		  	int urlIndex= url2index.get(url);
		    for (String word: browserWords){	  		
		  	  if(!word2index.containsKey(word)){
		  	    indexWord(word);
		  	  }
		  	  int wordIndex= word2index.get(word);
		  	  if (wordSet.add(word)){
		  	    urlIndexLists.get(wordIndex).add(urlIndex);
		  	  }
		  	}		  
		  	//System.out.println(url);
      }
		  	/*System.out.println("allURLs:\n" + allURLs);
		  	System.out.println("url2index:\n" + url2index);
		  	System.out.println("refCounts:\n" + refCounts);
		  	System.out.println("word2index:\n" + word2index);
		  	System.out.println("urlIndexLists:\n" + urlIndexLists);*/
	}

	@Override
	public String[] search(List<String> keyWords, int numResults) {
		
		//The search method is given a list of key words: keyWords
		//Associated with each key word is a list of page indices:  the pages which contain that word.
		// So when you are reading them in,  you would start at the beginning and read to the end as the disk spins
		
		//So you must use an Iterator for each list.  Since there are multiple keywords, you need an array Iterator<Integer>[]
		// Iterator into list of page ids for each key word.
		 //You need to initialize each entry  to the appropriate iterator
	    Iterator<Integer>[] pageIndexIterators =
	      (Iterator<Integer>[]) new Iterator[keyWords.size()];
	      
	    
	    // Current page index in each list, just ``behind'' the iterator.
	    //So you need an array  currentPageIndices to hold the current page indices:  the ones you most recently read from each Iterator.
	    int[] currentPageIndices= new int[keyWords.size()];
	    
	    // LEAST popular page is at top of heap so if heap has numResults
	    // elements and the next match is better than the least popular page
	    // in the queue, the least popular page can be thrown away.

	    PriorityQueue<Integer> bestPageIndices= new PriorityQueue<Integer>(numResults, new PageComparator());
		//   Write a loop to initialize the entries of pageIndexIterators.

	    for(int index=0; index < keyWords.size(); index++){
	      if(!word2index.containsKey(keyWords.get(index))){
	    		 	return new String[0];
	      }
	      if(word2index.containsKey(keyWords.get(index))){
	        int currentWord= word2index.get(keyWords.get(index));
	    	  pageIndexIterators[index]= urlIndexLists.get(currentWord).iterator();  
	      }
	      Iterator<String> iter= keyWords.iterator();
	    //look for words on web, if word is not available on web then the index is not there, 
	    //if the index is not there on the web
	    // then add it an array of strings
	    }
	
	    
//	    Implement the loop of search.  While updateSmallest is successful,
//	    check if the entries of currentPageIndices are all equal.  If so, you
//	    have a found a match, so save it in the queue.
	    
	    while(updateSmallest(currentPageIndices,pageIndexIterators)){
	    	 if(allEqual(currentPageIndices)){
	    		 if(bestPageIndices.size() <numResults){
	    		 bestPageIndices.add(currentPageIndices[0]);
	    	 }
	    		 if(bestPageIndices.size() == numResults){
	    			 if(bestPageIndices.peek() < currentPageIndices[0]){
	    				 bestPageIndices.poll();
	    				 bestPageIndices.offer(currentPageIndices[0]);
	    			 }
	    		 }
	    	 }
	    }
	    
	    String[] stringArray= new String[bestPageIndices.size()];
	    while(!bestPageIndices.isEmpty()){
	    	for(int x=bestPageIndices.size()-1; x != -1;x--){
	    		
	    	
	    		
	    		stringArray[x]= allURLs.get(bestPageIndices.poll());
	    		
	    	}
	    	
	    }
	    
	  return stringArray;	

	}
	
	public void indexUrl(String url){
	  allURLs.add(url);
	  url2index.put(url, allURLs.size()-1);
	  refCounts.add(0);
	}
	
	public void indexWord(String word){
	  allWords.add(word);
	  word2index.put(word, allWords.size()-1);
		// for each word, we will have a list of the indices of web pages
		//which contain it.  Each word has a List<Integer>
	  urlIndexLists.add(new ArrayList<Integer>());
	}
	
	
	
//	Implement the loop of search.  While updateSmallest is successful,
//    check if the entries of currentPageIndices are all equal.  If so, you
//    have a found a match, so save it in the queue.



public class PageComparator implements Comparator<Integer>{
//pass to the PriorityQueue of matching page indices. 
	@Override
	public int compare(Integer urlRefs1, Integer urlRefs2) {
	  return refCounts.get(urlRefs1) - refCounts.get(urlRefs2);
	}
}

/** Look through currentPageIndices for all the smallest elements.  For
each smallest element currentPageIndices[i], load the next element
from pageIndexIterators[i].  If pageIndexIterators[i] does not have a
next element, return false.  Otherwise, return true.
@param currentPageIndices array of current page indices
@param pageIndexIterators array of iterators with next page indices
@return true if all minimum page indices updates, false otherwise
*/
private boolean updateSmallest
(int[] currentPageIndices, Iterator<Integer>[] pageIndexIterators) {
	int smallest= currentPageIndices[0];
	

	for(int x= 0; x<currentPageIndices.length; x++){
		if(smallest > currentPageIndices[x]){
		smallest= currentPageIndices[x];
	    }
		
		for(int n=0; n < currentPageIndices.length; n++){
		if(smallest == currentPageIndices[x]){
			if(!pageIndexIterators[x].hasNext()){
				return false;
			} else{
				currentPageIndices[x]= pageIndexIterators[x].next();
			}
		}
	}
	}		
	return true;
	}


/** Check if all elements in an array are equal.
@param array an array of numbers
@return true if all are equal, false otherwise
*/
private boolean allEqual (int[] array) {
	int test= array[0];
	for(int x=1; x<array.length; x++){
		if(array[x] != test){
			return false;
		}
		
	}
	return true;
}
}


