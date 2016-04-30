(ns reward-system.web.views
	(:require [clojure.string :as str]
        	[hiccup.page :as hic-p]
			[reward-system.system.graph :as graph]
			[reward-system.system.util :as util]
			[reward-system.system.customerslist :as cl]))

; File directory
(def input-file "resources/files/input.txt")

; Graph that represent the company, its customers and the invitations (connections)
(def company (graph/build-graph
				(graph/->Graph {} (cl/->customers-list ()))
				input-file))

(defn gen-page-head
  [title]
  [:head
   [:title (str "Reward System: " title)]
   (hic-p/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/add-customer"} "Add a Customer"]
   " | "
   [:a {:href "/all-customers"} "View All Customers"]
   " | "
   [:a {:href "/rank"} "View rank of Customers"]
   " ]"])

(defn home-page
	"Builds the home page"
	[x]
	(hic-p/html5
		(gen-page-head "Home")
		header-links
		[:h1 "Reward System"]
		[:h2 "Customers sequence of invitation from file:"]
		[:h3 (util/input-html input-file)]))

(defn add-customer-page
	"Builds the page where customers can be invited."
	[]
	(hic-p/html5
		(gen-page-head "New customer")
		header-links
		[:h1 "Add a Customer"]
		[:form {:action "/add-customer" :method "POST"}
		[:p "src value: " [:input {:type "text" :name "src"}]]
		[:p "dst value: " [:input {:type "text" :name "dst"}]]
		[:p [:input {:type "submit" :value "Submit a customer"}]]]))

(defn add-customer-results-page
	"Adds customer to graph and build page."
	[{:keys [src dst]}]
	(hic-p/html5
		(gen-page-head "New customer")
		header-links
		(try
			(def company (graph/add-customer company src dst))
			[:h1 "Added a Customer"]
			[:p "Customer " src " invited customer " dst "."]
		(catch NumberFormatException e
			[:p "Those are not both numbers!"])
		(catch IllegalArgumentException e
			[:p "Only a customer in the company can invite others."]))))

(defn all-customers-page
	"Returns tha graph, each node and the nodes he is connected to."
	[]
	(hic-p/html5
    (gen-page-head "All customer")
    	header-links
    	[:h1 "List of customers and how they are related."]
    	[:h3 (graph/graph-html company)]))

(defn rank-page
	"Returns the page with the rank in JSON format."
	[]
	(hic-p/html5
	(gen-page-head "Rank")
    	header-links
    	[:h1 "Rank of all Customers in company by Score"]
    	[:h3 (graph/rank-html company)]))
