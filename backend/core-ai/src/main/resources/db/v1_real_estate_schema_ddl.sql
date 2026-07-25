-- ============================================
-- REAL ESTATE WEBSITE DATABASE SCHEMA (PostgreSQL)
-- IDEMPOTENT VERSION - Tables preserved
-- ============================================

-- Create ENUM types first (IF NOT EXISTS)
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_type_enum') THEN
            CREATE TYPE user_type_enum AS ENUM ('buyer', 'seller', 'agent', 'admin');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'property_type_enum') THEN
            CREATE TYPE property_type_enum AS ENUM ('house', 'apartment', 'condo', 'townhouse', 'land', 'commercial');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'listing_type_enum') THEN
            CREATE TYPE listing_type_enum AS ENUM ('sale', 'rent', 'lease');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'property_status_enum') THEN
            CREATE TYPE property_status_enum AS ENUM ('active', 'pending', 'sold', 'rented', 'inactive');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'inquiry_type_enum') THEN
            CREATE TYPE inquiry_type_enum AS ENUM ('viewing', 'info', 'offer', 'other');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'inquiry_status_enum') THEN
            CREATE TYPE inquiry_status_enum AS ENUM ('new', 'contacted', 'scheduled', 'closed');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'viewing_status_enum') THEN
            CREATE TYPE viewing_status_enum AS ENUM ('scheduled', 'confirmed', 'completed', 'cancelled');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'offer_status_enum') THEN
            CREATE TYPE offer_status_enum AS ENUM ('pending', 'accepted', 'rejected', 'countered', 'withdrawn');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'transaction_status_enum') THEN
            CREATE TYPE transaction_status_enum AS ENUM ('in_progress', 'completed', 'cancelled');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'feature_category_enum') THEN
            CREATE TYPE feature_category_enum AS ENUM ('interior', 'exterior', 'community', 'utilities');
        END IF;
    END $$;

-- Users table (buyers, sellers, agents)
CREATE TABLE IF NOT EXISTS users (
                                     user_id SERIAL PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     password_hash VARCHAR(255) NOT NULL,
                                     first_name VARCHAR(100) NOT NULL,
                                     last_name VARCHAR(100) NOT NULL,
                                     phone VARCHAR(20),
                                     user_type user_type_enum NOT NULL,
                                     profile_image VARCHAR(255),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Agents table (additional info for agents)
CREATE TABLE IF NOT EXISTS agents (
                                      agent_id SERIAL PRIMARY KEY,
                                      user_id INTEGER UNIQUE NOT NULL,
                                      license_number VARCHAR(100) UNIQUE NOT NULL,
                                      agency_name VARCHAR(255),
                                      years_experience INTEGER,
                                      specialization VARCHAR(255),
                                      bio TEXT,
                                      rating NUMERIC(2,1) DEFAULT 0.0,
                                      total_reviews INTEGER DEFAULT 0,
                                      total_sales INTEGER DEFAULT 0,
                                      FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Properties table
CREATE TABLE IF NOT EXISTS properties (
                                          property_id SERIAL PRIMARY KEY,
                                          owner_id INTEGER NOT NULL,
                                          agent_id INTEGER,
                                          title VARCHAR(255) NOT NULL,
                                          description TEXT,
                                          property_type property_type_enum NOT NULL,
                                          listing_type listing_type_enum NOT NULL,
                                          price NUMERIC(12,2) NOT NULL,
                                          address VARCHAR(500) NOT NULL,
                                          city VARCHAR(100) NOT NULL,
                                          state VARCHAR(100),
                                          country VARCHAR(100) NOT NULL,
                                          postal_code VARCHAR(20),
                                          latitude NUMERIC(10,8),
                                          longitude NUMERIC(11,8),
                                          bedrooms INTEGER,
                                          bathrooms NUMERIC(3,1),
                                          area_sqft NUMERIC(10,2),
                                          lot_size_sqft NUMERIC(10,2),
                                          year_built INTEGER,
                                          parking_spaces INTEGER DEFAULT 0,
                                          property_status property_status_enum DEFAULT 'active',
                                          views_count INTEGER DEFAULT 0,
                                          featured BOOLEAN DEFAULT FALSE,
                                          listed_date DATE NOT NULL,
                                          available_from DATE,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                          FOREIGN KEY (agent_id) REFERENCES agents(agent_id) ON DELETE SET NULL
);

-- Property images table
CREATE TABLE IF NOT EXISTS property_images (
                                               image_id SERIAL PRIMARY KEY,
                                               property_id INTEGER NOT NULL,
                                               image_url VARCHAR(500) NOT NULL,
                                               is_primary BOOLEAN DEFAULT FALSE,
                                               display_order INTEGER DEFAULT 0,
                                               uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE
);

-- Features/Amenities table
CREATE TABLE IF NOT EXISTS features (
                                        feature_id SERIAL PRIMARY KEY,
                                        feature_name VARCHAR(100) NOT NULL UNIQUE,
                                        feature_category feature_category_enum NOT NULL
);

-- Property features junction table
CREATE TABLE IF NOT EXISTS property_features (
                                                 property_id INTEGER,
                                                 feature_id INTEGER,
                                                 PRIMARY KEY (property_id, feature_id),
                                                 FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                                 FOREIGN KEY (feature_id) REFERENCES features(feature_id) ON DELETE CASCADE
);

-- Favorites/Saved properties
CREATE TABLE IF NOT EXISTS favorites (
                                         favorite_id SERIAL PRIMARY KEY,
                                         user_id INTEGER NOT NULL,
                                         property_id INTEGER NOT NULL,
                                         saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         UNIQUE (user_id, property_id),
                                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                         FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE
);

-- Inquiries/Contact requests
CREATE TABLE IF NOT EXISTS inquiries (
                                         inquiry_id SERIAL PRIMARY KEY,
                                         property_id INTEGER NOT NULL,
                                         user_id INTEGER,
                                         name VARCHAR(200) NOT NULL,
                                         email VARCHAR(255) NOT NULL,
                                         phone VARCHAR(20),
                                         message TEXT NOT NULL,
                                         inquiry_type inquiry_type_enum DEFAULT 'info',
                                         status inquiry_status_enum DEFAULT 'new',
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Property viewings/appointments
CREATE TABLE IF NOT EXISTS viewings (
                                        viewing_id SERIAL PRIMARY KEY,
                                        property_id INTEGER NOT NULL,
                                        user_id INTEGER NOT NULL,
                                        agent_id INTEGER,
                                        viewing_date TIMESTAMP NOT NULL,
                                        duration_minutes INTEGER DEFAULT 30,
                                        status viewing_status_enum DEFAULT 'scheduled',
                                        notes TEXT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                        FOREIGN KEY (agent_id) REFERENCES agents(agent_id) ON DELETE SET NULL
);

-- Offers table
CREATE TABLE IF NOT EXISTS offers (
                                      offer_id SERIAL PRIMARY KEY,
                                      property_id INTEGER NOT NULL,
                                      user_id INTEGER NOT NULL,
                                      offer_amount NUMERIC(12,2) NOT NULL,
                                      offer_status offer_status_enum DEFAULT 'pending',
                                      contingencies TEXT,
                                      closing_date DATE,
                                      message TEXT,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                      FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id SERIAL PRIMARY KEY,
                                            property_id INTEGER NOT NULL,
                                            buyer_id INTEGER NOT NULL,
                                            seller_id INTEGER NOT NULL,
                                            agent_id INTEGER,
                                            sale_price NUMERIC(12,2) NOT NULL,
                                            commission NUMERIC(10,2),
                                            transaction_status transaction_status_enum DEFAULT 'in_progress',
                                            closing_date DATE,
                                            contract_signed_date DATE,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                            FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                            FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                            FOREIGN KEY (agent_id) REFERENCES agents(agent_id) ON DELETE SET NULL
);

-- Reviews for agents
CREATE TABLE IF NOT EXISTS agent_reviews (
                                             review_id SERIAL PRIMARY KEY,
                                             agent_id INTEGER NOT NULL,
                                             reviewer_id INTEGER NOT NULL,
                                             transaction_id INTEGER,
                                             rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
                                             review_text TEXT,
                                             responsiveness_rating INTEGER CHECK (responsiveness_rating BETWEEN 1 AND 5),
                                             professionalism_rating INTEGER CHECK (professionalism_rating BETWEEN 1 AND 5),
                                             knowledge_rating INTEGER CHECK (knowledge_rating BETWEEN 1 AND 5),
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (agent_id) REFERENCES agents(agent_id) ON DELETE CASCADE,
                                             FOREIGN KEY (reviewer_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                             FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE SET NULL
);

-- Search history
CREATE TABLE IF NOT EXISTS search_history (
                                              search_id SERIAL PRIMARY KEY,
                                              user_id INTEGER,
                                              search_query TEXT,
                                              filters JSONB,
                                              results_count INTEGER,
                                              searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create indexes for better query performance (IF NOT EXISTS)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_properties_city ON properties(city);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_properties_status ON properties(property_status);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_properties_type ON properties(property_type);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_properties_price ON properties(price);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_properties_listed_date ON properties(listed_date);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_viewings_date ON viewings(viewing_date);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_offers_status ON offers(offer_status);

-- Create function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for automatic updated_at
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_properties_updated_at ON properties;
CREATE TRIGGER update_properties_updated_at BEFORE UPDATE ON properties
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_offers_updated_at ON offers;
CREATE TRIGGER update_offers_updated_at BEFORE UPDATE ON offers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();