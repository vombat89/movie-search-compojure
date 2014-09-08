;Example used for enive:
;http://rob-rowe.blogspot.com/2011/05/parsing-web-pages-with-clojure-and.html
;https://groups.google.com/group/enlive-clj/browse_thread/thread/41288d06fcaf83f0
;http://gnuvince.wordpress.com/2008/10/31/fetching-web-comics-with-clojure-part-1/


(ns adder.PageParser
  (:use [adder.image-reader])
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.string :as string]))


  (def url "http://www.rottentomatoes.com/")
  
  (defn get-page
  "Gets the html page from passed url"
  [url]
  (html/html-resource (java.net.URL. url))[:h3 :span :a])

  
(defn name+search
  "This function returns a sequence of h2 tags,where a.main is parsed from movieweb"
  [url]
  (html/select (get-page url) 
             [:h3 (html/attr= :class "nomargin"):a]))     

			 
(defn image+search
  "Function that returns a sequence of tags, where img is image parsed from movieweb"
  [url]
  (html/select (get-page url) 
             [:span (html/attr= :class "movieposter"):a]))    

			 
(defn genre+search
  "Function for parsing chosen genres"
  [url]
  (html/select (get-page url) 
             [:span (html/attr= :class "movie_year")]))     

(defn print-flick-name-content
  [url]
  (vec (flatten (map :content (name+search url)))))

(defn get-image-content
  [url]
  (vec (flatten (map #(re-find #"http.*jpg" %) (map :style (map :attrs (image+search url)))))))


;RATINGS SECTION
(def special-char "/")

(defn get-flick-grades ;pozovi java metodu jer sajt pita da li request dolazi od browsera.
  [url]
  ;(html/select (get-page url) ; will not work, site is checking if request is made by browser or an agent.
   ;            [:td :span])
  ;(get-rank-new url) ;java method call
  (string/replace (first (flatten (map :content (with-open [inputstream (-> (java.net.URL. url)
                            .openConnection
                            (doto (.setRequestProperty "User-Agent"
                                                       "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.0)"))
                            .getContent)]
  (html/select (html/html-resource inputstream) [(html/attr= :class "rank")]))))) "#" "")
    )

(defn alter-flickname "if flick name contains multiple words, they have to be separated with -" 
  [flick-name]  
  (string/replace (string/replace flick-name " " "-") special-char "-"))

(defn get-rating [name];izbaci # ovde
  (get-flick-grades (str "http://www.rottentomatoes.com/" (alter-flickname name))))  
    

(defn get-flick-rating4 "Returns rating for flicks. Result needs to be rearanged in ascending order." ;dodaj let bindovanje
  [flick-names]  
  (for [name flick-names]
   (let [flick-data (get-rating name)]      
     (if (nil? (get-rating name)) 100000000 
      (if (not= (string/trim (get-rating name)) "")
        (Double/parseDouble (string/replace (get-rating name) "," ""))
        100000)))))


;END OF RATINGS SECTION


;;DOWNLOADS SECTION
(defn make-search-URLs-for-download "makes urls for downloading flicks, using the altered flick name" 
  [flick-name]
  (str "http://www.rottentomatoes.com/" (string/replace (string/replace flick-name " " "+") "/" "%2F") "&Submit=Submit&searchFiles=1"))

(defn get-search-URLS "Gets the URL's. Foreach item in resulting sequence, there should be a call to get-flick-files function."
  [names-vector]
  (for [name names-vector]
    (make-search-URLs-for-download name)))

(defn get-flick-files "Using the download URL's tries to get actual files to download. If nothing is there, returns nil."
  [url-flick]  
  (for [url (vec (map :href (map :attrs (html/select (get-page url-flick)
               [:div (html/attr= :class "content") :ul :li :a]))))]
     (str "http://www.rottentomatoes.com/" url)))
;;END OF DOWNLOADS SECTION



(defn get-all-reading-links "Lists links to all series of chosen flick "
  [url]
  (vec (for [flick-url 
         (vec (map :href (map :attrs (name+search url))))]
     (str "http://www.rottentomatoes.com/" flick-url))))


