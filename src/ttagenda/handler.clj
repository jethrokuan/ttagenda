(ns ttagenda.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ttagenda.core :refer :all]))

(def auth-token "sb5poHImGVGSZs35AHppEgss")

(defroutes app-routes
  (POST "/" {:keys [params] :as  request}
        (if (and (= "/aa" (:command params))
                 (= auth-token (:token params)))
          (do (process-request params))
          {:status 400
           :content-type "text/plain"
           :body (str "Invalid Request:\n " request)}))
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
