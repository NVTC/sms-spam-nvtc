(ns sms-spam.core
    (:require
     [cheshire.core :as json]
     [clojure.string :as str]
     [clj-http.client :as http])
  (:gen-class))

(def template {:deliveryReportGates ["w8Tg0tC5"]
               :destinationTON "MSISDN"
               :source "evalg2016"
               :sourceTON "ALPHANUMERIC"
               :platformPartnerId "380"
               :platformId "SMSC"})

(defn payload [template message destination]
  (merge template {:destination destination
                   :userData message}))

(defn send-sms [uname passwd payload]
  (http/post "https://wsx.sp247.net/sms/send" {:form-params payload
                                               :content-type :json
                                               :basic-auth [uname passwd]}))

(defn send-all [uname passwd template message destinations]
  (doseq [destination destinations]
    (send-sms uname passwd (payload template message destination))))

(defn -main [uname passwd message-file phonenumber-file]
  (let [destinations (slurp phonenumber-file)
        message (slurp message-file)]
    (send-all uname passwd template message (str/split-lines destinations))))
