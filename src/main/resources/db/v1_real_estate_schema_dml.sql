-- ============================================
-- DML OPERATIONS (INSERT, UPDATE, DELETE, SELECT)
-- ============================================

-- INSERT Examples
-- ----------------

-- Insert users
INSERT INTO users (email, password_hash, first_name, last_name, phone, user_type) VALUES
                                                                                      ('buyer1@email.com', 'hashed_pass_123', 'John', 'Buyer', '+1234567890', 'buyer'),
                                                                                      ('seller1@email.com', 'hashed_pass_456', 'Sarah', 'Seller', '+1987654321', 'seller'),
                                                                                      ('agent1@email.com', 'hashed_pass_789', 'Mike', 'Agent', '+1555555555', 'agent');

-- Insert agent details
INSERT INTO agents (user_id, license_number, agency_name, years_experience, specialization, bio) VALUES
    (3, 'LIC12345', 'Prime Realty Group', 8, 'Residential Properties', 'Experienced agent specializing in luxury homes');

-- Insert properties
INSERT INTO properties (owner_id, agent_id, title, description, property_type, listing_type, price,
                        address, city, state, country, postal_code, bedrooms, bathrooms, area_sqft, year_built, listed_date) VALUES
                                                                                                                                 (2, 1, 'Modern 3BR House in Downtown', 'Beautiful modern home with open floor plan', 'house', 'sale',
                                                                                                                                  450000.00, '123 Main Street', 'Los Angeles', 'California', 'USA', '90001', 3, 2.5, 2000.00, 2015, '2026-01-01'),
                                                                                                                                 (2, 1, 'Luxury Apartment with City View', 'Stunning penthouse apartment', 'apartment', 'rent',
                                                                                                                                  3500.00, '456 High Street', 'New York', 'New York', 'USA', '10001', 2, 2.0, 1500.00, 2020, '2026-01-05');

-- Insert property images
INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                    (1, '/images/property1_main.jpg', TRUE, 1),
                                                                                    (1, '/images/property1_kitchen.jpg', FALSE, 2),
                                                                                    (1, '/images/property1_bedroom.jpg', FALSE, 3),
                                                                                    (2, '/images/property2_main.jpg', TRUE, 1);

-- Insert features
INSERT INTO features (feature_name, feature_category) VALUES
                                                          ('Hardwood Floors', 'interior'),
                                                          ('Granite Countertops', 'interior'),
                                                          ('Walk-in Closet', 'interior'),
                                                          ('Swimming Pool', 'exterior'),
                                                          ('Garden', 'exterior'),
                                                          ('Parking Garage', 'exterior'),
                                                          ('Gym', 'community'),
                                                          ('Security System', 'utilities'),
                                                          ('Central AC', 'utilities');

-- Link features to properties
INSERT INTO property_features (property_id, feature_id) VALUES
                                                            (1, 1), (1, 2), (1, 3), (1, 4), (1, 6),
                                                            (2, 1), (2, 2), (2, 7), (2, 8), (2, 9);

-- Insert favorites
INSERT INTO favorites (user_id, property_id) VALUES
                                                 (1, 1), (1, 2);

-- Insert inquiries
INSERT INTO inquiries (property_id, user_id, name, email, phone, message, inquiry_type) VALUES
    (1, 1, 'John Buyer', 'buyer1@email.com', '+1234567890',
     'I am interested in viewing this property. Please contact me.', 'viewing');

-- Insert viewings
INSERT INTO viewings (property_id, user_id, agent_id, viewing_date, status) VALUES
    (1, 1, 1, '2026-02-10 14:00:00', 'scheduled');

-- Insert offers
INSERT INTO offers (property_id, user_id, offer_amount, message) VALUES
    (1, 1, 440000.00, 'Cash offer, flexible closing date');

-- Insert transactions
INSERT INTO transactions (property_id, buyer_id, seller_id, agent_id, sale_price, commission, closing_date) VALUES
    (1, 1, 2, 1, 445000.00, 13350.00, '2026-03-15');

-- Insert agent reviews
INSERT INTO agent_reviews (agent_id, reviewer_id, transaction_id, rating, review_text,
                           responsiveness_rating, professionalism_rating, knowledge_rating) VALUES
    (1, 1, 1, 5, 'Excellent agent! Very professional and helpful throughout the process.', 5, 5, 5);

