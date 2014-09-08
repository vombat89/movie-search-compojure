





;I tried to add CSS, but I was not successfull:
;http://stackoverflow.com/questions/2130448/getting-started-with-css-in-compojure
(ns adder.pages
  (:use compojure.core 
        [hiccup core form page element]
        [adder.PageParser]
        [adder.reorder]
        [adder.dbManagement]
        [adder.image-reader]
		[adder.middleware])
  (:require [compojure.handler :as handler])
			
  (:import [java.io InputStream InputStreamReader BufferedReader]
           [java.net URL HttpURLConnection])); add middleware for params

(defn view-layout2 [& content]
  (html5 [:body content]))

(defn view-layout [& content]
  (html5 		
			[:head				
				[:title "Movie Helper"]
    (include-css "/CSS_Style.css")
	(include-js "http://code.jquery.com/jquery-2.1.1.min.js")
				;;[:link {:href "src/resources/CSS_Style.css" :rel "stylesheet" :type "text/css"}]
    ]
			[:body content ;(include-css "/CSS_Style.css")
    ]
    ))


  
	   
(defn view-input [] 
  (view-layout 
	
    [:h2 "Find your Movie"] 
	[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"  }
    (form-to [:post "/"]
		
      [:br] 
	  [:br]
      (text-field {:placeholder "Enter movie name" } :a) [:br] 
      (submit-button "Search")
      [:br]
	  [:br]
	  [:br]
	  (str "<table border= 2px solid black bgcolor=#F2FB78 table align=center >"
         
           (str "<tr>" 
		   "<td>" ">>> Your Watchlist <<<" "</td>"
		   "</tr>"
			"<tr>"
		   "<td>" "<a href=" (str "http://localhost:3000/addfilm") ">" "Add a new film to your watchlist" "</a>" "</td>"
		   "</tr>"
		   "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/addnewmovie-series") ">" "Add new movie-series to your watchlist" "</a>" "</td>"
           "</tr>"            
         "</table>"))
		 [:br]
		 [:br]
		 [:br]
		 (str "<table border= 2px solid black bgcolor=#F2FB78 table align=center width=340px>"
         
           (str "<tr>" 
		   "<td>" ">>> Browse your movies here <<<" "</td>"
		   "</tr>"
			"<tr>"
		   "<td>" "<a href=" (str "http://localhost:3000/listmovies") ">" "View complete list of your movies" "</a>" "</td>"
		   "</tr>"
		   "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/listseries") ">" "View list of your movie series" "</a>" "</td>"
           "</tr>"            
         "</table>"))
			
		 )]
		 ))
      

(defn change-window [] 
  (view-layout 
    [:h2 "You have successfully made changes to your Watchlist"] 
	[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"  }
    (form-to [:post "/"]
             
      
	  [:br]
      [:br]
	  [:br]
	  (str "<table border= 2px solid black bgcolor=#F2FB78 table align=center >"
         
           (str "<tr>" 
		   "<td>" ">>> From here, you can: <<<" "</td>"
		   "</tr>"
		   "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/listmovies") ">" "View complete list of your movies" "</a>" "</td>"
           "</tr>"
		   "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/listseries") ">" "View list of your movie series" "</a>" "</td>"
           "</tr>"
			"<tr>"
		   "<td>" "<a href=" (str "http://localhost:3000/addfilm") ">" "Add a new film to your watchlist" "</a>" "</td>"
		   "</tr>"
		   "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/addnewmovie-series") ">" "Add new movie-series to your watchlist" "</a>" "</td>"
           "</tr>"  
         "</table>"))
		 )]
		 ))
	  
 	
	
	
	  
(defn create-flick-url [a]   
  (str "http://www.rottentomatoes.com/search/?search=" a ""))


(defn flick-vec [a]
           (vec (let [flick-url (create-flick-url a)
                     flick-names (print-flick-name-content flick-url)]
                     (mapper-gen4 flick-names
                     (get-image-content flick-url) 
					 
					 (get-all-reading-links flick-url)
					 (get-search-URLS flick-names)
					 
                      ))) )

(defn view-output2 [a]    
  (view-layout
   
    [:h2 "Search results"]
	[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"  }
    [:form {:method "post" :action "/"}               
      (interleave
        (for [flick (flick-vec a)]        
        (label :flick_name (:name flick))) 
		
        (for [flick-name (flick-vec a)][:br])
		
        (for [flick-image (flick-vec a)]
           [:img {:id "img_movie" :src (:image flick-image)}])      
        
        (for [flick-read-link (flick-vec a)]
          (link-to (:read-link flick-read-link) "Read it here!"))
        
        (for [flick (flick-vec a)]
        [:br]))]]))

;series cruds

(defn view-movie-series "view all available movie-series" []
  (html5 		
			[:head				
				[:title "movie-series film collection"]
    ;(include-css "/css/CSS_Style.css")
    ]
			[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
    (str "<table border=2px bgcolor= #F2FB78 table align=center>"
         ( apply str (seq (for [movie-series (list-series)]
		 
           (str "<tr>" 
           "<td>"(:series_production movie-series) "</td>"
           "<td>"(:series_name movie-series) "</td>"           
          ;"<td>" (link-to (:read-link ) "See it here!") "</td>" ;
           "<td>" "<a href=" (str "http://localhost:3000/listmoviesfran/" (:series_id movie-series)) ">" "See all saved movies from this movie-series" "</a>" "</td>"
           "<td>" "<a href=" (str "http://localhost:3000/editmovie-series/" (:series_id movie-series)) ">" "Edit movie-series" "</a>" "</td>"
           "<td>" "<a href=" (str "http://localhost:3000/deletemovie-series/" (:series_id movie-series)) ">" "Delete movie-series" "</a>" "</td>"
           ;"<td>" "<a href=" (str "http://localhost:3000/addnewmovie-series") ">" "Add new movie-series" "</a>" "</td>"
           "</tr>"            
           )
         )))
         "<tr>"
           "<td>" "<a href=" (str "http://localhost:3000/addnewmovie-series") ">" "Add new movie-series" "</a>" "</td>"
           "</tr>"
         "</table>")]))

(defn save-movie-series [] 
  (view-layout 
    
    [:h2 "Add new movie-series"] 
	[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"  }
    (form-to [:post "/addnewmovie-series"]
            
      (label "lblseriesName" "Movie-series title: ")
      (text-field  :txtname) [:br] 
      
      (label "lblProducer" "Production name: ")
      (text-field  :txtproname) [:br]
      
      (submit-button "Save"))
	  [:br]
	  [:br]
	  (str "<a href=http://localhost:3000>"  ">> Return to Homepage <<" "</a>")
	  ]))

(defn update-movie-series [sid] 
  (view-layout 
    [:h2 "Update movie-series"] 
	[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"  }
    (form-to [:put (format "/editmovie-series/%s" sid)];editmovie-series
            
      (label "lblseriesName" "Movie-series name: ")
      (text-field  :txtname (first (map :series_name (list-one-series sid)))) [:br] 
      
      (label "lblProducer" "Production name: ")
      (text-field  :txtproname (first (map :series_production (list-one-series sid)))) [:br]
      
      (submit-button "Update"))
	  [:br]
	  [:br]
	  (str "<a href=http://localhost:3000>"  ">> Return to Homepage <<" "</a>")
	  ]))

(defn delete-movie-series [sid] 
  (view-layout 
    [:h2 "Delete selected movie-series"] 
    (form-to [:delete (format "/deletemovie-series/%s" sid)];deletemovie-series
            [:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
      (label "lblseriesName" "Movie-series name: ")
      (text-field  :txtname (first (map :series_name (list-one-series sid)))) [:br] 
      
      (label "lblProducer" "Production name: ")
      (text-field  :txtproname (first (map :series_production (list-one-series sid)))) [:br]
      
      (submit-button "Delete")])))
;end

;;film CRUDS

(defn view-movies "view all available movies" []   
  (html5 		
			[:head				
				[:title "movie-series film collection"]
    ;(include-css "/css/CSS_Style.css")
    ]
			[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
    (str "<table border= 2px solid black bgcolor=#F2FB78 table align=center>"
         ( apply str (seq (for [film (list-movies)]
           (str "<tr>" 
           "<td>"(:name film) "</td>"
           "<td>"(:duration film) "</td>"
           "<td>"(:Producer film) "</td>"
           "<td>" (str "<a href=http://localhost:3000/viewimage/" (:id film) ">"  "Check it here" "</a>") "</td>"
           "<td>" (str "<a href=http://localhost:3000/addfilm/" (:id film) ">"  "Edit information" "</a>") "</td>"
           "<td>" (str "<a href=http://localhost:3000/deletefilm/" (:id  film) ">"  "Delete movie" "</a>") "</td>"
           "</tr>"))))
         "<tr>" 
         "<td>" (str "<a href=http://localhost:3000/addfilm >"  "Add movie to your watchlist" "</a>") 
         "</td>" 
         "</tr>" 
         "</table>")]))


(defn view-movies-from-one-movie-series "view all available movies from one movie-series" [sid]   
  (html5 		
			[:head				
				[:title "movie-series film collection"]
    ;(include-css "/css/CSS_Style.css")
    ]
			[:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
    (str "<table border= 2px solid black bgcolor=#F2FB78 table align=center>"
         ( apply str (seq (for [film (list-all-movies-from-serie sid)]
           (str "<tr>" 
           "<td>"(:name film) "</td>"
           "<td>"(:duration film) "</td>"
           "<td>"(:Producer film) "</td>"
           "<td>" (str "<a href=http://localhost:3000/viewimage/" (:id film) ">"  "Check it here!" "</a>") "</td>"
           ;"<td>" (str "<a href=http://localhost:3000/addfilm >"  "Add new here!" "</a>") "</td>"
           "<td>" (str "<a href=http://localhost:3000/addfilm/" (:id film) ">"  "Update it here!" "</a>") "</td>"
           "<td>" (str "<a href=http://localhost:3000/deletefilm/" (:id  film) ">"  "Delete it here!" "</a>") "</td>"
           "</tr>")
         ))) "<tr>" "<td>" (str "<a href=http://localhost:3000/addnewfilmtofran/" sid ">"  "Add new here!" "</a>") "</td>" "</tr>"
         "</table>")]))

(defn view-film-input-update [cid] 
  (view-layout 
    [:h2 "Update film"] 
    (form-to [:put (format "/addfilm/%s" cid)]
            [:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
      (label "movname" "Film title: ")
      (text-field  :txtname (first (map :name (list-one-film cid)))) [:br] ;(first (:name (list-one-film cid)))
      
      (label "proname" "Producer name: ")
      (text-field  :txtproname (first (map :producer (list-one-film cid)))) [:br] 
      (label "lbllocation" "File location: ")
      (text-field :txtlocation (first (map :location (list-one-film cid))))[:br]
      
      (label "duration" "Duration(minutes): ")
      (text-field  :txtduration  (first (map :duration (list-one-film cid))))
      [:br] 
      (submit-button "Update")])))

(defn view-film-input-delete [cid] 
  (view-layout 
    [:h2 "Delete film"] 
    (form-to [:delete (format "/deletefilm/%s" cid)]
            [:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
      (label "movname" "Film title: ")
      (text-field  :txtname (first (map :name (list-one-film cid)))) [:br] 
      
      (label "proname" "Producer name: ")
      (text-field  :txtproname (first (map :Producer (list-one-film cid)))) [:br] 
      (label "location" "File location: ")
      (text-field :txtlocation (first (map :location (list-one-film cid))))[:br]      
      (label "duration" "Duration(minutes): ")
      (text-field  :txtduration  (first (map :duration (list-one-film cid))))
      [:br] 
      (submit-button "Delete")])))

(defn view-film-input [] 
  (view-layout 
  [:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
    [:h2 "Add new film"] 
    (form-to [:post "/addfilm" ]
            
      (label "movname" "Film title: ")
      (text-field  :txtname) [:br] 
      
      (label "proname" "Producer name: ")
      (text-field  :txtproname) [:br] 
      (label "location" "File location: ")
      (text-field :txtlocation)[:br]
      
      (label "duration" "Duration(minutes): ")
      (text-field  :txtduration)[:br]
      (submit-button "Save"))
	  [:br]
	  [:br]
	  (str "<a href=http://localhost:3000>"  ">> Return to Homepage <<" "</a>")
	  ]))
	  

(defn view-film-input-in-movie-series [sid] 
  (view-layout 
  [:body {:style "font: 14pt/16pt helvetica; background-color: #F2FB78; padding-top:100px; text-align: center"}
    [:h2 "Add new film"] 
    (form-to [:post (format "/addnewfilmtofran/%s" sid)]
            
      (label "movname" "Film title: ")
      (text-field  :txtname) [:br] 
      
      (label "proname" "Producer name: ")
      (text-field  :txtproname) [:br] 
      (label "location" "File location: ")
      (text-field :txtlocation)[:br]
      
      (label "duration" "Duration(minutes): ")
      (text-field  :txtduration)[:br]
      (submit-button "Save"))]))
;;end

(defn view-image [loc] "Open film as an image"
  (html5 [:head				
				[:title "Manage selected movie-series"]]
         [:body] (str (open-image2 loc))))

(defroutes main-routes            
   (GET "/" [] 
      (view-input))
  
  (POST "/" [a] 
	(do
       (println "A" a )
  (view-output2 a)))
  
  
  
  (GET "/listmovies" []
        (view-movies))
  
  (GET "/addfilm/:cid" [cid] (view-film-input-update cid))
  (PUT "/addfilm/:cid" [cid txtname txtproname txtlocation txtduration] (update-flick (read-string cid) {:name txtname :Producer txtproname :location txtlocation :duration txtduration}) (change-window))
  
  (GET "/addnewfilmtofran/:sid" [sid] (view-film-input-in-movie-series sid))
  (POST "/addnewfilmtofran/:sid" [sid txtname txtproname txtlocation txtduration]
        (insert-flick txtname txtproname txtlocation txtduration (read-string sid))(change-window))
  
  (GET "/addfilm" [] (view-film-input))
  (POST "/addfilm" [txtname txtproname txtlocation txtduration]
        (insert-flick txtname txtproname txtlocation txtduration 90) (change-window))
  
  (GET "/deletefilm/:cid" [cid] (view-film-input-delete cid))
  (DELETE "/deletefilm/:cid" [cid] (delete-flick cid)(change-window))
  
  (GET "/listmoviesfran/:sid" [sid]
        (view-movies-from-one-movie-series sid))
  ;;end
  
  ;;movie-series
  (GET "/listseries" []
        (view-movie-series))
  
  (GET "/addnewmovie-series" [] (save-movie-series))
  (POST "/addnewmovie-series" [txtname txtproname] (insert-serie txtname txtproname)(change-window))
  
  (GET "/editmovie-series/:sid" [sid] (update-movie-series sid));editmovie-series
  (PUT "/editmovie-series/:sid" [sid txtname txtproname] (update-serie (read-string sid) {:series_name txtname :series_production txtproname})(change-window))
  
  (GET "/deletemovie-series/:sid" [sid] (delete-movie-series sid))
  (DELETE "/deletemovie-series/:sid" [sid] (delete-series sid)(change-window))
  ;;end
  
  (GET "/viewimage/:cid" [cid] (view-image (load-image-path cid)));
  
  
  
  
  
  )
   

(def app (handler/site main-routes)) ; wrap the params to allow destructuring to work
