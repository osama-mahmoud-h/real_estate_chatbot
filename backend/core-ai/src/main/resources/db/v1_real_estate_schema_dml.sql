-- ============================================
-- DML OPERATIONS (INSERT, UPDATE, DELETE, SELECT)
-- IDEMPOTENT VERSION WITH EGYPT PROPERTY DATA
-- Using UPSERT (INSERT ... ON CONFLICT) for idempotency
-- ============================================

-- ============================================
-- INSERT/UPDATE USERS (Idempotent)
-- ============================================

INSERT INTO users (email, password_hash, first_name, last_name, phone, user_type) VALUES
                                                                                      ('ahmed.buyer@email.com', 'hashed_pass_123', 'Ahmed', 'Hassan', '+201001234567', 'buyer'),
                                                                                      ('fatma.buyer@email.com', 'hashed_pass_456', 'Fatma', 'Ibrahim', '+201002345678', 'buyer'),
                                                                                      ('khaled.seller@email.com', 'hashed_pass_789', 'Khaled', 'Mohamed', '+201003456789', 'seller'),
                                                                                      ('nadia.seller@email.com', 'hashed_pass_101', 'Nadia', 'Ali', '+201004567890', 'seller'),
                                                                                      ('mostafa.agent@email.com', 'hashed_pass_112', 'Mostafa', 'Rahman', '+201005678901', 'agent'),
                                                                                      ('dina.agent@email.com', 'hashed_pass_131', 'Dina', 'Sayed', '+201006789012', 'agent'),
                                                                                      ('youssef.admin@email.com', 'hashed_pass_415', 'Youssef', 'Farouk', '+201007890123', 'admin')
ON CONFLICT (email) DO UPDATE SET
                                  password_hash = EXCLUDED.password_hash,
                                  first_name = EXCLUDED.first_name,
                                  last_name = EXCLUDED.last_name,
                                  phone = EXCLUDED.phone,
                                  user_type = EXCLUDED.user_type,
                                  updated_at = CURRENT_TIMESTAMP;

-- ============================================
-- INSERT/UPDATE AGENTS (Idempotent)
-- ============================================

INSERT INTO agents (user_id, license_number, agency_name, years_experience, specialization, bio) VALUES
                                                                                                     ((SELECT user_id FROM users WHERE email = 'mostafa.agent@email.com'),
                                                                                                      'EG-RE-2024-001', 'Elite Real Estate Egypt', 12, 'Luxury Properties',
                                                                                                      'Specializing in luxury villas and apartments in New Cairo, Sheikh Zayed, and the North Coast. Over 500 successful transactions.'),
                                                                                                     ((SELECT user_id FROM users WHERE email = 'dina.agent@email.com'),
                                                                                                      'EG-RE-2024-002', 'Nile Property Group', 8, 'Residential & Commercial',
                                                                                                      'Expert in residential properties in Cairo, Alexandria, and emerging communities. Helping buyers find their dream homes since 2016.')
ON CONFLICT (license_number) DO UPDATE SET
                                           agency_name = EXCLUDED.agency_name,
                                           years_experience = EXCLUDED.years_experience,
                                           specialization = EXCLUDED.specialization,
                                           bio = EXCLUDED.bio;

-- ============================================
-- INSERT/UPDATE PROPERTIES (Idempotent)
-- ============================================

-- Property 1: Luxury Villa in New Cairo
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, lot_size_sqft, year_built, parking_spaces,
    property_status, featured, listed_date, available_from
) VALUES (
             (SELECT user_id FROM users WHERE email = 'khaled.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-001'),
             'Luxury 5BR Villa in New Cairo | Private Pool & Garden',
             'Stunning contemporary villa located in the prestigious Fifth Settlement, New Cairo. Features include a private swimming pool, landscaped garden, high-end finishes, smart home system, and panoramic views. Perfect for families seeking luxury and comfort.',
             'house', 'sale', 8500000.00,
             '42 El Tesseen Street, Fifth Settlement', 'New Cairo', 'Cairo', 'Egypt', '11835',
             30.030456, 31.468789,
             5, 4.5, 6500.00, 12000.00, 2021, 4,
             'active', TRUE, '2026-01-15', '2026-02-01'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 2: Modern Apartment in Zamalek
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, year_built, parking_spaces,
    property_status, featured, listed_date, available_from
) VALUES (
             (SELECT user_id FROM users WHERE email = 'khaled.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-001'),
             'Stylish 3BR Apartment in Zamalek | Nile View',
             'Elegant apartment in one of Cairo''s most prestigious neighborhoods. Features stunning Nile views, open-plan living area, modern kitchen with high-end appliances, and a private balcony. Walking distance to restaurants, cafes, and cultural attractions.',
             'apartment', 'sale', 4200000.00,
             '15 Shagaret El Dorr Street, Zamalek', 'Cairo', 'Cairo', 'Egypt', '11211',
             30.058199, 31.223789,
             3, 2.5, 1850.00, 2005, 2,
             'active', TRUE, '2026-01-10', '2026-01-15'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 3: Beachfront Penthouse in North Coast
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, year_built, parking_spaces,
    property_status, featured, listed_date, available_from
) VALUES (
             (SELECT user_id FROM users WHERE email = 'nadia.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-002'),
             'Beachfront Penthouse | Sidi Abdel Rahman | North Coast',
             'Luxurious penthouse with direct beach access in the exclusive Sidi Abdel Rahman resort. Features panoramic Mediterranean Sea views, private roof terrace, infinity pool, and premium finishes. Ideal for summer getaways or year-round living.',
             'apartment', 'sale', 12500000.00,
             'Sidi Abdel Rahman Bay', 'North Coast', 'Matrouh', 'Egypt', '51732',
             31.117234, 27.859876,
             4, 3.5, 3200.00, 2022, 3,
             'active', TRUE, '2026-01-20', '2026-06-01'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 4: Family Home in Sheikh Zayed
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, lot_size_sqft, year_built, parking_spaces,
    property_status, featured, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'nadia.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-002'),
             'Spacious 4BR Home in Sheikh Zayed | Garden & Pool',
             'Beautiful family home in one of Sheikh Zayed''s most desirable compounds. Features a large garden, swimming pool, BBQ area, and ample space for entertaining. Close to international schools, shopping centers, and major highways.',
             'house', 'sale', 5500000.00,
             'Compound 88, Sheikh Zayed City', '6th of October City', 'Giza', 'Egypt', '12588',
             30.032456, 31.023789,
             4, 3.0, 4500.00, 8000.00, 2019, 3,
             'active', FALSE, '2026-01-05'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 5: Commercial Office Space in Downtown Cairo
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, year_built, parking_spaces,
    property_status, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'khaled.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-001'),
             'Prime Office Space in Downtown Cairo | Central Location',
             'Professional office space in the heart of Downtown Cairo. Ideal for law firms, consulting agencies, or corporate headquarters. Fully renovated with modern finishes, high-speed internet infrastructure, and conference room facilities.',
             'commercial', 'sale', 3000000.00,
             '10 Kasr El Nil Street, Downtown', 'Cairo', 'Cairo', 'Egypt', '11513',
             30.044456, 31.235789,
             NULL, 2.0, 1500.00, 1998, 1,
             'active', '2026-01-25'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 6: Luxury Apartment for Rent in Garden City
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, year_built, parking_spaces,
    property_status, listed_date, available_from
) VALUES (
             (SELECT user_id FROM users WHERE email = 'nadia.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-002'),
             'Elegant 3BR Apartment for Rent | Garden City',
             'Charming apartment in the diplomatic district of Garden City. Features high ceilings, original architectural details, spacious rooms, and a private balcony. Walking distance to the Nile, embassies, and fine dining restaurants.',
             'apartment', 'rent', 35000.00,
             '5 Garden City Street', 'Cairo', 'Cairo', 'Egypt', '11519',
             30.050456, 31.228789,
             3, 2.0, 2100.00, 1970, 1,
             'active', '2026-01-30', '2026-03-01'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 7: Land for Development in New Administrative Capital
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, lot_size_sqft, year_built, parking_spaces,
    property_status, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'khaled.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-001'),
             'Prime Land Plot for Development | New Administrative Capital',
             'Strategic land plot in the R7 district of the New Administrative Capital. Perfect for residential or commercial development. Located near the Central Business District, government buildings, and proposed metro stations.',
             'land', 'sale', 1500000.00,
             'R7 District, New Administrative Capital', 'New Capital', 'Cairo', 'Egypt', '11765',
             30.012456, 31.489789,
             NULL, NULL, NULL, 5000.00, NULL, 0,
             'active', '2026-01-12'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 8: Townhouse in Maadi
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, lot_size_sqft, year_built, parking_spaces,
    property_status, featured, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'nadia.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-002'),
             'Charming 3BR Townhouse in Degla, Maadi',
             'Lovely townhouse in the serene Degla neighborhood of Maadi. Features a private garden, rooftop terrace, and modern interiors. Close to Maadi''s vibrant dining scene, international schools, and the Cairo metro system.',
             'townhouse', 'sale', 3800000.00,
             '22 Road 9, Degla, Maadi', 'Maadi', 'Cairo', 'Egypt', '11728',
             29.968456, 31.239789,
             3, 2.5, 2800.00, 1500.00, 2008, 2,
             'active', FALSE, '2026-01-18'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 9: Modern Apartment in Alexandria
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, year_built, parking_spaces,
    property_status, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'khaled.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-001'),
             'Sea View Apartment in San Stefano, Alexandria',
             'Stunning apartment with panoramic Mediterranean Sea views in the upscale San Stefano district. Walking distance to the beach, San Stefano Mall, and fine dining. Perfect for those seeking a coastal lifestyle.',
             'apartment', 'sale', 2800000.00,
             'San Stefano Promenade', 'Alexandria', 'Alexandria', 'Egypt', '21521',
             31.268456, 29.972789,
             2, 2.0, 1400.00, 2015, 1,
             'active', '2026-01-22'
         ) ON CONFLICT (property_id) DO NOTHING;

-- Property 10: Modern Villa in October Gardens
INSERT INTO properties (
    owner_id, agent_id, title, description, property_type, listing_type, price,
    address, city, state, country, postal_code, latitude, longitude,
    bedrooms, bathrooms, area_sqft, lot_size_sqft, year_built, parking_spaces,
    property_status, listed_date
) VALUES (
             (SELECT user_id FROM users WHERE email = 'nadia.seller@email.com'),
             (SELECT agent_id FROM agents WHERE license_number = 'EG-RE-2024-002'),
             'Modern 4BR Villa in October Gardens | Golf Course View',
             'Stunning villa overlooking the golf course in October Gardens. Features contemporary design, open-plan living, smart home technology, private pool, and landscaped garden. Part of a secure gated community with 24/7 security.',
             'house', 'sale', 7200000.00,
             'October Gardens, 6th of October City', '6th of October City', 'Giza', 'Egypt', '12573',
             30.015456, 31.015789,
             4, 3.5, 5200.00, 9000.00, 2020, 4,
             'active', '2026-01-28'
         ) ON CONFLICT (property_id) DO NOTHING;

-- ============================================
-- INSERT/UPDATE PROPERTY IMAGES (Idempotent)
-- ============================================

-- We'll use a DO block to handle images idempotently
DO $$
    DECLARE
        prop_id INTEGER;
    BEGIN
        -- Property 1 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/newcairo_villa_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/newcairo_villa_2.jpg', FALSE, 2),
                                                                                                (prop_id, '/images/egypt/newcairo_villa_3.jpg', FALSE, 3),
                                                                                                (prop_id, '/images/egypt/newcairo_villa_4.jpg', FALSE, 4),
                                                                                                (prop_id, '/images/egypt/newcairo_villa_5.jpg', FALSE, 5)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 2 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Stylish 3BR Apartment in Zamalek%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/zamalek_apartment_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/zamalek_apartment_2.jpg', FALSE, 2),
                                                                                                (prop_id, '/images/egypt/zamalek_apartment_3.jpg', FALSE, 3)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 3 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Beachfront Penthouse%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/northcoast_penthouse_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/northcoast_penthouse_2.jpg', FALSE, 2),
                                                                                                (prop_id, '/images/egypt/northcoast_penthouse_3.jpg', FALSE, 3)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 4 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Spacious 4BR Home in Sheikh Zayed%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/sheikhzayed_house_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/sheikhzayed_house_2.jpg', FALSE, 2)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 5 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Prime Office Space in Downtown Cairo%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/downtown_office_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/downtown_office_2.jpg', FALSE, 2)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 6 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Elegant 3BR Apartment for Rent%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                (prop_id, '/images/egypt/gardencity_apartment_1.jpg', TRUE, 1)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 7 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Prime Land Plot for Development%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                (prop_id, '/images/egypt/newcapital_land_1.jpg', TRUE, 1)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 8 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Charming 3BR Townhouse in Degla%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/maadi_townhouse_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/maadi_townhouse_2.jpg', FALSE, 2)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 9 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Sea View Apartment in San Stefano%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/alexandria_apartment_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/alexandria_apartment_2.jpg', FALSE, 2)
            ON CONFLICT DO NOTHING;
        END IF;

        -- Property 10 Images
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Modern 4BR Villa in October Gardens%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            INSERT INTO property_images (property_id, image_url, is_primary, display_order) VALUES
                                                                                                (prop_id, '/images/egypt/octobergardens_villa_1.jpg', TRUE, 1),
                                                                                                (prop_id, '/images/egypt/octobergardens_villa_2.jpg', FALSE, 2),
                                                                                                (prop_id, '/images/egypt/octobergardens_villa_3.jpg', FALSE, 3)
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT/UPDATE FEATURES (Idempotent)
-- ============================================

INSERT INTO features (feature_name, feature_category) VALUES
                                                          -- Interior Features
                                                          ('Hardwood Floors', 'interior'),
                                                          ('Granite Countertops', 'interior'),
                                                          ('Walk-in Closet', 'interior'),
                                                          ('Open Floor Plan', 'interior'),
                                                          ('High Ceilings', 'interior'),
                                                          ('Fireplace', 'interior'),
                                                          ('Smart Home System', 'interior'),
                                                          ('Central Air Conditioning', 'interior'),
                                                          ('Marble Flooring', 'interior'),
                                                          ('Built-in Wardrobes', 'interior'),

                                                          -- Exterior Features
                                                          ('Swimming Pool', 'exterior'),
                                                          ('Garden', 'exterior'),
                                                          ('Parking Garage', 'exterior'),
                                                          ('Balcony', 'exterior'),
                                                          ('Rooftop Terrace', 'exterior'),
                                                          ('BBQ Area', 'exterior'),
                                                          ('Fenced Yard', 'exterior'),
                                                          ('Waterfront', 'exterior'),

                                                          -- Community Features
                                                          ('Gym', 'community'),
                                                          ('Security System', 'community'),
                                                          ('Clubhouse', 'community'),
                                                          ('Playground', 'community'),
                                                          ('Swimming Pool (Community)', 'community'),
                                                          ('Golf Course View', 'community'),
                                                          ('Gated Community', 'community'),
                                                          ('24/7 Security', 'community'),

                                                          -- Utilities
                                                          ('Central AC', 'utilities'),
                                                          ('Solar Panels', 'utilities'),
                                                          ('Backup Generator', 'utilities'),
                                                          ('High-Speed Internet', 'utilities'),
                                                          ('Elevator', 'utilities')
ON CONFLICT (feature_name) DO UPDATE SET
    feature_category = EXCLUDED.feature_category;

-- ============================================
-- LINK FEATURES TO PROPERTIES (Idempotent)
-- ============================================

DO $$
    DECLARE
        prop_id INTEGER;
        feat_id INTEGER;
    BEGIN
        -- Property 1: New Cairo Villa
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            -- Get feature IDs and insert
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Granite Countertops', 'Walk-in Closet', 'Open Floor Plan', 'Smart Home System',
                                                                  'Central Air Conditioning', 'Swimming Pool', 'Garden', 'Rooftop Terrace',
                                                                  'Security System', 'Gated Community', 'Central AC')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 2: Zamalek Apartment
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Stylish 3BR Apartment in Zamalek%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Hardwood Floors', 'Open Floor Plan', 'High Ceilings', 'Central Air Conditioning',
                                                                  'Built-in Wardrobes', 'Balcony', 'Security System', 'Central AC', 'Elevator')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 3: North Coast Penthouse
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Beachfront Penthouse%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Hardwood Floors', 'Granite Countertops', 'Walk-in Closet', 'Smart Home System',
                                                                  'Swimming Pool', 'Rooftop Terrace', 'Waterfront', 'Security System',
                                                                  'Swimming Pool (Community)', 'High-Speed Internet')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 4: Sheikh Zayed House
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Spacious 4BR Home in Sheikh Zayed%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Open Floor Plan', 'Fireplace', 'Central Air Conditioning', 'Swimming Pool',
                                                                  'Garden', 'BBQ Area', 'Fenced Yard', 'Security System', 'Clubhouse',
                                                                  'Gated Community', 'Central AC')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 5: Downtown Office
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Prime Office Space in Downtown Cairo%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Open Floor Plan', 'High Ceilings', 'Central Air Conditioning', 'Security System',
                                                                  'Central AC', 'Backup Generator', 'Elevator')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 6: Garden City Apartment
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Elegant 3BR Apartment for Rent%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Hardwood Floors', 'High Ceilings', 'Marble Flooring', 'Built-in Wardrobes',
                                                                  'Balcony', 'Security System', 'Central AC', 'High-Speed Internet')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 7: New Capital Land
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Prime Land Plot for Development%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN ('Gated Community')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 8: Maadi Townhouse
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Charming 3BR Townhouse in Degla%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Hardwood Floors', 'Granite Countertops', 'Walk-in Closet', 'Open Floor Plan',
                                                                  'Central Air Conditioning', 'Garden', 'Fenced Yard', 'Security System',
                                                                  'Gated Community', 'Central AC')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 9: Alexandria Apartment
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Sea View Apartment in San Stefano%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Hardwood Floors', 'Open Floor Plan', 'Central Air Conditioning', 'Built-in Wardrobes',
                                                                  'Balcony', 'Waterfront', 'Security System', 'Central AC', 'Elevator')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;

        -- Property 10: October Gardens Villa
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Modern 4BR Villa in October Gardens%' LIMIT 1;
        IF prop_id IS NOT NULL THEN
            FOR feat_id IN SELECT feature_id FROM features WHERE feature_name IN
                                                                 ('Granite Countertops', 'Walk-in Closet', 'Open Floor Plan', 'Smart Home System',
                                                                  'Central Air Conditioning', 'Swimming Pool', 'Garden', 'Rooftop Terrace',
                                                                  'Security System', 'Golf Course View', 'Gated Community', 'Central AC')
                LOOP
                    INSERT INTO property_features (property_id, feature_id) VALUES (prop_id, feat_id)
                    ON CONFLICT (property_id, feature_id) DO NOTHING;
                END LOOP;
        END IF;
    END $$;

-- ============================================
-- INSERT FAVORITES (Idempotent)
-- ============================================

DO $$
    DECLARE
        user1_id INTEGER;
        user2_id INTEGER;
        prop1_id INTEGER;
        prop2_id INTEGER;
        prop3_id INTEGER;
        prop4_id INTEGER;
        prop6_id INTEGER;
        prop8_id INTEGER;
    BEGIN
        SELECT user_id INTO user1_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT user_id INTO user2_id FROM users WHERE email = 'fatma.buyer@email.com';

        SELECT property_id INTO prop1_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;
        SELECT property_id INTO prop2_id FROM properties WHERE title LIKE 'Stylish 3BR Apartment in Zamalek%' LIMIT 1;
        SELECT property_id INTO prop3_id FROM properties WHERE title LIKE 'Beachfront Penthouse%' LIMIT 1;
        SELECT property_id INTO prop4_id FROM properties WHERE title LIKE 'Spacious 4BR Home in Sheikh Zayed%' LIMIT 1;
        SELECT property_id INTO prop6_id FROM properties WHERE title LIKE 'Elegant 3BR Apartment for Rent%' LIMIT 1;
        SELECT property_id INTO prop8_id FROM properties WHERE title LIKE 'Charming 3BR Townhouse in Degla%' LIMIT 1;

        IF user1_id IS NOT NULL THEN
            INSERT INTO favorites (user_id, property_id) VALUES
                                                             (user1_id, prop1_id), (user1_id, prop3_id), (user1_id, prop6_id)
            ON CONFLICT (user_id, property_id) DO NOTHING;
        END IF;

        IF user2_id IS NOT NULL THEN
            INSERT INTO favorites (user_id, property_id) VALUES
                                                             (user2_id, prop2_id), (user2_id, prop4_id), (user2_id, prop8_id)
            ON CONFLICT (user_id, property_id) DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT INQUIRIES (Idempotent)
-- ============================================

DO $$
    DECLARE
        user1_id INTEGER;
        user2_id INTEGER;
        prop1_id INTEGER;
        prop3_id INTEGER;
        prop4_id INTEGER;
    BEGIN
        SELECT user_id INTO user1_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT user_id INTO user2_id FROM users WHERE email = 'fatma.buyer@email.com';

        SELECT property_id INTO prop1_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;
        SELECT property_id INTO prop3_id FROM properties WHERE title LIKE 'Beachfront Penthouse%' LIMIT 1;
        SELECT property_id INTO prop4_id FROM properties WHERE title LIKE 'Spacious 4BR Home in Sheikh Zayed%' LIMIT 1;

        IF user1_id IS NOT NULL AND prop1_id IS NOT NULL THEN
            INSERT INTO inquiries (property_id, user_id, name, email, phone, message, inquiry_type) VALUES
                (prop1_id, user1_id, 'Ahmed Hassan', 'ahmed.buyer@email.com', '+201001234567',
                 'I am very interested in the New Cairo villa. Could you arrange a viewing? I would like to see the property this weekend.', 'viewing')
            ON CONFLICT DO NOTHING;
        END IF;

        IF user2_id IS NOT NULL AND prop3_id IS NOT NULL THEN
            INSERT INTO inquiries (property_id, user_id, name, email, phone, message, inquiry_type) VALUES
                (prop3_id, user2_id, 'Fatma Ibrahim', 'fatma.buyer@email.com', '+201002345678',
                 'Is the penthouse in Sidi Abdel Rahman still available? We are looking for a summer home and would love to schedule a visit.', 'info')
            ON CONFLICT DO NOTHING;
        END IF;

        IF user1_id IS NOT NULL AND prop4_id IS NOT NULL THEN
            INSERT INTO inquiries (property_id, user_id, name, email, phone, message, inquiry_type) VALUES
                (prop4_id, user1_id, 'Ahmed Hassan', 'ahmed.buyer@email.com', '+201001234567',
                 'We are interested in the Sheikh Zayed property. Could you provide more details about the compound and nearby schools?', 'info')
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT VIEWINGS (Idempotent)
-- ============================================

DO $$
    DECLARE
        user1_id INTEGER;
        user2_id INTEGER;
        agent1_id INTEGER;
        agent2_id INTEGER;
        prop1_id INTEGER;
        prop2_id INTEGER;
        prop4_id INTEGER;
    BEGIN
        SELECT user_id INTO user1_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT user_id INTO user2_id FROM users WHERE email = 'fatma.buyer@email.com';
        SELECT agent_id INTO agent1_id FROM agents WHERE license_number = 'EG-RE-2024-001';
        SELECT agent_id INTO agent2_id FROM agents WHERE license_number = 'EG-RE-2024-002';

        SELECT property_id INTO prop1_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;
        SELECT property_id INTO prop2_id FROM properties WHERE title LIKE 'Stylish 3BR Apartment in Zamalek%' LIMIT 1;
        SELECT property_id INTO prop4_id FROM properties WHERE title LIKE 'Spacious 4BR Home in Sheikh Zayed%' LIMIT 1;

        IF user1_id IS NOT NULL AND agent1_id IS NOT NULL AND prop1_id IS NOT NULL THEN
            INSERT INTO viewings (property_id, user_id, agent_id, viewing_date, status, notes) VALUES
                (prop1_id, user1_id, agent1_id, '2026-02-10 14:00:00', 'scheduled',
                 'Client arriving by car. Need to confirm gate access.')
            ON CONFLICT DO NOTHING;
        END IF;

        IF user2_id IS NOT NULL AND agent1_id IS NOT NULL AND prop2_id IS NOT NULL THEN
            INSERT INTO viewings (property_id, user_id, agent_id, viewing_date, status, notes) VALUES
                (prop2_id, user2_id, agent1_id, '2026-02-12 11:30:00', 'confirmed',
                 'Couple looking for a family home.')
            ON CONFLICT DO NOTHING;
        END IF;

        IF user1_id IS NOT NULL AND agent2_id IS NOT NULL AND prop4_id IS NOT NULL THEN
            INSERT INTO viewings (property_id, user_id, agent_id, viewing_date, status, notes) VALUES
                (prop4_id, user1_id, agent2_id, '2026-02-15 16:00:00', 'scheduled',
                 'Interested in the garden and pool area.')
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT OFFERS (Idempotent)
-- ============================================

DO $$
    DECLARE
        user1_id INTEGER;
        prop1_id INTEGER;
    BEGIN
        SELECT user_id INTO user1_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT property_id INTO prop1_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;

        IF user1_id IS NOT NULL AND prop1_id IS NOT NULL THEN
            INSERT INTO offers (property_id, user_id, offer_amount, message) VALUES
                (prop1_id, user1_id, 8300000.00, 'Cash offer, flexible closing date. Ready to close within 30 days.')
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT TRANSACTIONS (Idempotent)
-- ============================================

DO $$
    DECLARE
        buyer_id INTEGER;
        seller_id INTEGER;
        agent1_id INTEGER;
        prop_id INTEGER;
    BEGIN
        SELECT user_id INTO buyer_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT user_id INTO seller_id FROM users WHERE email = 'khaled.seller@email.com';
        SELECT agent_id INTO agent1_id FROM agents WHERE license_number = 'EG-RE-2024-001';
        SELECT property_id INTO prop_id FROM properties WHERE title LIKE 'Luxury 5BR Villa in New Cairo%' LIMIT 1;

        IF buyer_id IS NOT NULL AND seller_id IS NOT NULL AND agent1_id IS NOT NULL AND prop_id IS NOT NULL THEN
            INSERT INTO transactions (property_id, buyer_id, seller_id, agent_id, sale_price, commission, closing_date) VALUES
                (prop_id, buyer_id, seller_id, agent1_id, 8300000.00, 249000.00, '2026-03-15')
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;

-- ============================================
-- INSERT AGENT REVIEWS (Idempotent)
-- ============================================

DO $$
    DECLARE
        reviewer_id INTEGER;
        agent1_id INTEGER;
        trans_id INTEGER;
    BEGIN
        SELECT user_id INTO reviewer_id FROM users WHERE email = 'ahmed.buyer@email.com';
        SELECT agent_id INTO agent1_id FROM agents WHERE license_number = 'EG-RE-2024-001';
        SELECT transaction_id INTO trans_id FROM transactions WHERE sale_price = 8300000.00 LIMIT 1;

        IF reviewer_id IS NOT NULL AND agent1_id IS NOT NULL AND trans_id IS NOT NULL THEN
            INSERT INTO agent_reviews (agent_id, reviewer_id, transaction_id, rating, review_text,
                                       responsiveness_rating, professionalism_rating, knowledge_rating) VALUES
                (agent1_id, reviewer_id, trans_id, 5,
                 'Excellent agent! Very professional and helpful throughout the entire process. Highly recommended!',
                 5, 5, 5)
            ON CONFLICT DO NOTHING;
        END IF;
    END $$;