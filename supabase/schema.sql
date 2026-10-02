-- Profiles table
CREATE TABLE IF NOT EXISTS public.profiles (
    id uuid REFERENCES auth.users ON DELETE CASCADE PRIMARY KEY,
    username text,
    avatar text,
    bio text,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own profile." ON public.profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile." ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- User Stats table
CREATE TABLE IF NOT EXISTS public.user_stats (
    user_id uuid REFERENCES auth.users ON DELETE CASCADE PRIMARY KEY,
    best_wpm integer DEFAULT 0,
    best_accuracy integer DEFAULT 0,
    total_tests integer DEFAULT 0,
    total_words integer DEFAULT 0,
    total_characters integer DEFAULT 0,
    total_play_time integer DEFAULT 0,
    current_streak integer DEFAULT 0,
    longest_streak integer DEFAULT 0,
    updated_at timestamp with time zone DEFAULT now()
);

ALTER TABLE public.user_stats ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own stats." ON public.user_stats
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can update their own stats." ON public.user_stats
    FOR ALL USING (auth.uid() = user_id);

-- Achievements table (definitions)
CREATE TABLE IF NOT EXISTS public.achievements (
    id text PRIMARY KEY,
    key text UNIQUE,
    name text,
    description text,
    icon text,
    requirement jsonb
);

ALTER TABLE public.achievements ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow read access to all for achievements" ON public.achievements
    FOR SELECT USING (true);

-- User Achievements table
CREATE TABLE IF NOT EXISTS public.user_achievements (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id uuid REFERENCES auth.users ON DELETE CASCADE,
    achievement_id text REFERENCES public.achievements(id) ON DELETE CASCADE,
    progress bigint DEFAULT 0,
    unlocked boolean DEFAULT false,
    unlocked_at timestamp with time zone,
    UNIQUE(user_id, achievement_id)
);

ALTER TABLE public.user_achievements ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own achievements." ON public.user_achievements
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can update their own achievements." ON public.user_achievements
    FOR ALL USING (auth.uid() = user_id);

-- Typing Results table
CREATE TABLE IF NOT EXISTS public.typing_results (
    id text PRIMARY KEY,
    user_id uuid REFERENCES auth.users ON DELETE CASCADE,
    mode text,
    wpm integer,
    accuracy integer,
    duration integer,
    word_count integer,
    character_count integer,
    language text,
    difficulty text,
    created_at timestamp with time zone DEFAULT now()
);

ALTER TABLE public.typing_results ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own typing results." ON public.typing_results
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own typing results." ON public.typing_results
    FOR INSERT WITH CHECK (auth.uid() = user_id);

-- Daily Activity table
CREATE TABLE IF NOT EXISTS public.daily_activity (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id uuid REFERENCES auth.users ON DELETE CASCADE,
    activity_date date NOT NULL,
    tests_completed integer DEFAULT 0,
    words_typed integer DEFAULT 0,
    characters_typed integer DEFAULT 0,
    play_time integer DEFAULT 0,
    updated_at timestamp with time zone DEFAULT now(),
    UNIQUE(user_id, activity_date)
);

ALTER TABLE public.daily_activity ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own daily activity." ON public.daily_activity
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can update their own daily activity." ON public.daily_activity
    FOR ALL USING (auth.uid() = user_id);

-- Trigger to create profile on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.profiles (id, username, avatar)
  VALUES (new.id, new.raw_user_meta_data->>'username', new.raw_user_meta_data->>'avatar');

  INSERT INTO public.user_stats (user_id)
  VALUES (new.id);

  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
