(ns adder.reorder)

(defstruct flick :name :image)

(defn mapper-gen2
  [names images] (sort-by :name (map #(hash-map :name %1 :image %2 ) names images)))

(defn mapper-gen3
  [names images read-link] (sort-by :name (map #(hash-map :name %1 :image %2 :read-link %3)
                                                                   names images read-link )))

     
(defn mapper-gen4
  [names images read-link download-link] (sort-by :names (map #(:name %1 :image %2 :read-link %3 :download-link %4) names images read-link download-link)))
