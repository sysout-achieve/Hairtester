package com.example.msi.connect;

public class ReviewItem {

        private String review_title;
        private String review;
        private Double review_rat;
        private String review_id;
        private String review_date;

        public ReviewItem(String review_title, String review, Double review_rat, String review_id, String review_date){
            this.review_title = review_title;
            this.review = review;
            this.review_rat = review_rat;
            this.review_id = review_id;
            this.review_date = review_date;
        }

        public String getReview_title() {
            return review_title;
        }
        public String getReview_content() {
            return review;
        }
        public Double getReview_rat() {
            return review_rat;
        }
        public String  getReview_id(){
            return review_id;
        }
        public String getReview_date(){
            return review_date;
        }
}
