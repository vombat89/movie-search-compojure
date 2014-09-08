(use [ring.adapter.jetty]
    [compojure.core]
    [ring.middleware.params]))
(require 'adder.pages)

(let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
  (run-jetty #'adder.pages/app {:port port}))
