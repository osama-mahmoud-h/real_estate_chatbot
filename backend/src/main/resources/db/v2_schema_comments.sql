-- ============================================
-- SCHEMA COMMENTS FOR LLM UNDERSTANDING
-- Add semantic descriptions to tables and columns
-- ============================================

-- ============================================
-- TABLE COMMENTS
-- ============================================

COMMENT ON TABLE users IS 'User accounts including buyers, sellers, agents, and admins';
COMMENT ON TABLE agents IS 'Real estate agent profiles with license and performance info';
COMMENT ON TABLE properties IS 'Real estate property listings with all details';
COMMENT ON TABLE property_images IS 'Images associated with property listings';
COMMENT ON TABLE features IS 'Available amenities and features (pool, garage, etc.)';
COMMENT ON TABLE property_features IS 'Junction table linking properties to their features';
COMMENT ON TABLE favorites IS 'Properties saved/bookmarked by users';
COMMENT ON TABLE inquiries IS 'Contact requests and questions about properties';
COMMENT ON TABLE viewings IS 'Scheduled property viewing appointments';
COMMENT ON TABLE offers IS 'Purchase or rental offers made on properties';
COMMENT ON TABLE transactions IS 'Completed real estate transactions';
COMMENT ON TABLE agent_reviews IS 'Reviews and ratings for agents from clients';
COMMENT ON TABLE search_history IS 'User search queries and filters for analytics';

-- ============================================
-- USERS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN users.user_id IS 'Unique identifier for the user';
COMMENT ON COLUMN users.email IS 'User email address (unique, used for login)';
COMMENT ON COLUMN users.password_hash IS 'Hashed password (never expose)';
COMMENT ON COLUMN users.first_name IS 'User first name';
COMMENT ON COLUMN users.last_name IS 'User last name';
COMMENT ON COLUMN users.phone IS 'Contact phone number';
COMMENT ON COLUMN users.user_type IS 'Role: buyer, seller, agent, or admin';
COMMENT ON COLUMN users.profile_image IS 'URL to profile picture';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';
COMMENT ON COLUMN users.updated_at IS 'Last profile update timestamp';

-- ============================================
-- AGENTS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN agents.agent_id IS 'Unique identifier for the agent';
COMMENT ON COLUMN agents.user_id IS 'Reference to user account';
COMMENT ON COLUMN agents.license_number IS 'Real estate license number';
COMMENT ON COLUMN agents.agency_name IS 'Name of the agency they work for';
COMMENT ON COLUMN agents.years_experience IS 'Years of experience in real estate';
COMMENT ON COLUMN agents.specialization IS 'Area of expertise (luxury, commercial, etc.)';
COMMENT ON COLUMN agents.bio IS 'Agent biography/description';
COMMENT ON COLUMN agents.rating IS 'Average rating from 0.0 to 5.0';
COMMENT ON COLUMN agents.total_reviews IS 'Total number of reviews received';
COMMENT ON COLUMN agents.total_sales IS 'Total completed transactions';

-- ============================================
-- PROPERTIES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN properties.property_id IS 'Unique identifier for the property';
COMMENT ON COLUMN properties.owner_id IS 'User who owns/listed the property';
COMMENT ON COLUMN properties.agent_id IS 'Agent managing the listing (optional)';
COMMENT ON COLUMN properties.title IS 'Property listing title';
COMMENT ON COLUMN properties.description IS 'Detailed property description';
COMMENT ON COLUMN properties.property_type IS 'Type: house, apartment, condo, townhouse, land, commercial';
COMMENT ON COLUMN properties.listing_type IS 'Purpose: sale, rent, or lease';
COMMENT ON COLUMN properties.price IS 'Listing price in local currency';
COMMENT ON COLUMN properties.address IS 'Full street address';
COMMENT ON COLUMN properties.city IS 'City name';
COMMENT ON COLUMN properties.state IS 'State or province';
COMMENT ON COLUMN properties.country IS 'Country name';
COMMENT ON COLUMN properties.postal_code IS 'ZIP or postal code';
COMMENT ON COLUMN properties.latitude IS 'GPS latitude coordinate';
COMMENT ON COLUMN properties.longitude IS 'GPS longitude coordinate';
COMMENT ON COLUMN properties.bedrooms IS 'Number of bedrooms (NULL for land/commercial)';
COMMENT ON COLUMN properties.bathrooms IS 'Number of bathrooms (can be decimal like 2.5)';
COMMENT ON COLUMN properties.area_sqft IS 'Interior living area in square feet';
COMMENT ON COLUMN properties.lot_size_sqft IS 'Total lot size in square feet';
COMMENT ON COLUMN properties.year_built IS 'Year the property was constructed';
COMMENT ON COLUMN properties.parking_spaces IS 'Number of parking spots available';
COMMENT ON COLUMN properties.property_status IS 'Status: active, pending, sold, rented, inactive';
COMMENT ON COLUMN properties.views_count IS 'Number of times listing was viewed';
COMMENT ON COLUMN properties.featured IS 'Whether property is featured/promoted';
COMMENT ON COLUMN properties.listed_date IS 'Date property was listed';
COMMENT ON COLUMN properties.available_from IS 'Date property becomes available';
COMMENT ON COLUMN properties.created_at IS 'Record creation timestamp';
COMMENT ON COLUMN properties.updated_at IS 'Last update timestamp';

-- ============================================
-- PROPERTY_IMAGES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN property_images.image_id IS 'Unique identifier for the image';
COMMENT ON COLUMN property_images.property_id IS 'Property this image belongs to';
COMMENT ON COLUMN property_images.image_url IS 'URL to the image file';
COMMENT ON COLUMN property_images.is_primary IS 'Whether this is the main/cover image';
COMMENT ON COLUMN property_images.display_order IS 'Order in which to display images';
COMMENT ON COLUMN property_images.uploaded_at IS 'When the image was uploaded';

-- ============================================
-- FEATURES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN features.feature_id IS 'Unique identifier for the feature';
COMMENT ON COLUMN features.feature_name IS 'Name of the feature (e.g., Swimming Pool)';
COMMENT ON COLUMN features.feature_category IS 'Category: interior, exterior, community, utilities';

-- ============================================
-- PROPERTY_FEATURES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN property_features.property_id IS 'Property with this feature';
COMMENT ON COLUMN property_features.feature_id IS 'Feature the property has';

-- ============================================
-- FAVORITES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN favorites.favorite_id IS 'Unique identifier for the favorite';
COMMENT ON COLUMN favorites.user_id IS 'User who saved the property';
COMMENT ON COLUMN favorites.property_id IS 'Property that was saved';
COMMENT ON COLUMN favorites.saved_at IS 'When the property was saved';

-- ============================================
-- INQUIRIES TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN inquiries.inquiry_id IS 'Unique identifier for the inquiry';
COMMENT ON COLUMN inquiries.property_id IS 'Property the inquiry is about';
COMMENT ON COLUMN inquiries.user_id IS 'User making the inquiry (if logged in)';
COMMENT ON COLUMN inquiries.name IS 'Name of person making inquiry';
COMMENT ON COLUMN inquiries.email IS 'Contact email for response';
COMMENT ON COLUMN inquiries.phone IS 'Contact phone number';
COMMENT ON COLUMN inquiries.message IS 'Inquiry message content';
COMMENT ON COLUMN inquiries.inquiry_type IS 'Type: viewing, info, offer, other';
COMMENT ON COLUMN inquiries.status IS 'Status: new, contacted, scheduled, closed';
COMMENT ON COLUMN inquiries.created_at IS 'When inquiry was submitted';

-- ============================================
-- VIEWINGS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN viewings.viewing_id IS 'Unique identifier for the viewing';
COMMENT ON COLUMN viewings.property_id IS 'Property to be viewed';
COMMENT ON COLUMN viewings.user_id IS 'User requesting the viewing';
COMMENT ON COLUMN viewings.agent_id IS 'Agent conducting the viewing';
COMMENT ON COLUMN viewings.viewing_date IS 'Scheduled date and time';
COMMENT ON COLUMN viewings.duration_minutes IS 'Expected duration in minutes';
COMMENT ON COLUMN viewings.status IS 'Status: scheduled, confirmed, completed, cancelled';
COMMENT ON COLUMN viewings.notes IS 'Additional notes about the viewing';
COMMENT ON COLUMN viewings.created_at IS 'When viewing was scheduled';

-- ============================================
-- OFFERS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN offers.offer_id IS 'Unique identifier for the offer';
COMMENT ON COLUMN offers.property_id IS 'Property the offer is for';
COMMENT ON COLUMN offers.user_id IS 'User making the offer';
COMMENT ON COLUMN offers.offer_amount IS 'Offered price amount';
COMMENT ON COLUMN offers.offer_status IS 'Status: pending, accepted, rejected, countered, withdrawn';
COMMENT ON COLUMN offers.contingencies IS 'Conditions attached to the offer';
COMMENT ON COLUMN offers.closing_date IS 'Proposed closing date';
COMMENT ON COLUMN offers.message IS 'Message to seller';
COMMENT ON COLUMN offers.created_at IS 'When offer was submitted';
COMMENT ON COLUMN offers.updated_at IS 'Last update to offer';

-- ============================================
-- TRANSACTIONS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN transactions.transaction_id IS 'Unique identifier for the transaction';
COMMENT ON COLUMN transactions.property_id IS 'Property being transacted';
COMMENT ON COLUMN transactions.buyer_id IS 'User buying the property';
COMMENT ON COLUMN transactions.seller_id IS 'User selling the property';
COMMENT ON COLUMN transactions.agent_id IS 'Agent handling the transaction';
COMMENT ON COLUMN transactions.sale_price IS 'Final sale price';
COMMENT ON COLUMN transactions.commission IS 'Agent commission amount';
COMMENT ON COLUMN transactions.transaction_status IS 'Status: in_progress, completed, cancelled';
COMMENT ON COLUMN transactions.closing_date IS 'Actual closing date';
COMMENT ON COLUMN transactions.contract_signed_date IS 'When contract was signed';
COMMENT ON COLUMN transactions.created_at IS 'When transaction record was created';

-- ============================================
-- AGENT_REVIEWS TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN agent_reviews.review_id IS 'Unique identifier for the review';
COMMENT ON COLUMN agent_reviews.agent_id IS 'Agent being reviewed';
COMMENT ON COLUMN agent_reviews.reviewer_id IS 'User writing the review';
COMMENT ON COLUMN agent_reviews.transaction_id IS 'Related transaction (if any)';
COMMENT ON COLUMN agent_reviews.rating IS 'Overall rating from 1 to 5';
COMMENT ON COLUMN agent_reviews.review_text IS 'Written review content';
COMMENT ON COLUMN agent_reviews.responsiveness_rating IS 'Rating for responsiveness (1-5)';
COMMENT ON COLUMN agent_reviews.professionalism_rating IS 'Rating for professionalism (1-5)';
COMMENT ON COLUMN agent_reviews.knowledge_rating IS 'Rating for market knowledge (1-5)';
COMMENT ON COLUMN agent_reviews.created_at IS 'When review was submitted';

-- ============================================
-- SEARCH_HISTORY TABLE COLUMNS
-- ============================================

COMMENT ON COLUMN search_history.search_id IS 'Unique identifier for the search';
COMMENT ON COLUMN search_history.user_id IS 'User who performed the search';
COMMENT ON COLUMN search_history.search_query IS 'Text search query';
COMMENT ON COLUMN search_history.filters IS 'Applied filters as JSON';
COMMENT ON COLUMN search_history.results_count IS 'Number of results returned';
COMMENT ON COLUMN search_history.searched_at IS 'When search was performed';