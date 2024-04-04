-- SEQUENCE: public.service_categories_id_seq

DROP SEQUENCE IF EXISTS public.service_categories_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.service_categories_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- SEQUENCE: public.services_id_seq

DROP SEQUENCE IF EXISTS public.services_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.services_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- SEQUENCE: public.order_statuses_id_seq

DROP SEQUENCE IF EXISTS public.order_statuses_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.order_statuses_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- SEQUENCE: public.orders_id_seq

DROP SEQUENCE IF EXISTS public.orders_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.orders_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- SEQUENCE: public.feedbacks_id_seq

DROP SEQUENCE IF EXISTS public.feedbacks_id_seq;

CREATE SEQUENCE IF NOT EXISTS public.feedbacks_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;