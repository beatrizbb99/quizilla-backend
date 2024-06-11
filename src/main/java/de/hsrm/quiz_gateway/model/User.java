package de.hsrm.quiz_gateway.model;

public class User {
        private long id;
        private String name;
        private String email;
        private String password;
        private String createdAt;


        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getCreatedAt() {
            return createdAt;
        }
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }


        @Override
        public String toString() {
            return "ID: " + id +
                    ", Name: " + name +
                    ", Email: " + email +
                    ", Password: " + password +
                    ", Created At: " + createdAt;
        }
    }