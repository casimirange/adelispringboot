package com.adeli.adelispringboot.configuration.globalConfiguration;

import javax.servlet.http.HttpServletRequest;

public class ApplicationConstant {

    public static final String ENTREPRISE_NAME = "ADELI";

    public static final String SUBJECT_EMAIL_PAY = "ADELI - Accès espace membres";

    public static final String SUBJECT_EMAIL_OPT = "ADELI - Code OTP";

    public static final String SUBJECT_EMAIL_NEW_USER = "ADELI - Bienvenue";

    public static final String SUBJECT_EMAIL_NEW_SESSION = "ADELI - Nouvelle Session";

    public static final String SUBJECT_EMAIL_DISCIPLINE = "ADELI - ";

    public static final String SUBJECT_EMAIL_MANGWA = "ADELI - Sortie d'argent";

    public static final String SUBJECT_EMAIL_END_SESSION = "ADELI - Fin de Session";

    public static final String SUBJECT_EMAIL_NEW_ORDER = "ADELI - Nouvelle Commande : #Ref";

    public static final String SUBJECT_EMAIL_ORDER_STOCKAGE = "ADELI - Ordre de stockage";

    public static final String SUBJECT_EMAIL_ORDER_SUPPLY = "ADELI - Ordre d'approvisionnement";

    public static final String SUBJECT_EMAIL_ORDER_TRANSFER= "ADELI - Ordre de de transfert de cartons";

    public static final String SUBJECT_EMAIL_DEMANDE_OPPOSITION= "ADELI - Demande d'opposition";

    public static final String SUBJECT_EMAIL_CREDIT_NOTE= "ADELI - Note de credit";

    public static final String SUBJECT_EMAIL_ACCEPT_COUPON= "ADELI - Acceptation du coupon ";

    public static final String SUBJECT_EMAIL_VALID_CREDIT_NOTE= "ADELI - Validation d'une note de crédit ";

    public static final String SUBJECT_EMAIL_MODIFY_ORDER = "ADELI - Modification de la Commande : #Ref";

    public static final String SUBJECT_EMAIL_MODIFY_SEANCE = "ADELI - Séance du ";
    public static final String SUBJECT_EMAIL_COMPTE_RENDU = "ADELI - Compte rendu de la séance du : #";

    public static final String SUBJECT_EMAIL_CANCEL_ORDER = "ADELI - Annulation de la Commande : #Ref";

    public static final String SUBJECT_EMAIL_CANCEL_MULTI_ORDER = "ADELI - Annulation en lots des Commandes ";

    public static final String SUBJECT_EMAIL_NEW_INVOICE = "ADELI - Nouvelle Proforma : #Ref";

    public static final String SUBJECT_EMAIL_NEW_INVOICE2 = "ADELI - Nouvelle Préfacture : #Ref";

    public static final String SUBJECT_EMAIL_EXPORT_ORDERS_EXCEL = "ADELI - Liste des commandes";

    public static final String SUBJECT_EMAIL_EXPORT_COUPONS_EXCEL = "ADELI - Liste des coupons";

    public static final String SUBJECT_EMAIL_NEW_RECEIVED = "ADELI - Reçue de paiement : #Ref";

    public static final String SUBJECT_EMAIL_NEW_FACTURE = "ADELI - Facture : #Ref";

    public static final String SUBJECT_EMAIL_PAY_TEMPORAIRE = "ADELI - Accès Temporaire";

    public static final String SUBJECT_EMAIL_VILID_PROFIL = "ADELI - Validation de profil ";

    public static final String SUBJECT_EMAIL_VILID_PROFIL_NOT_VALIDE = "ADELI - Profil non valide";

    public static final String TEMPLATE_EMAIL_ENTREPRISE_MEMBRE = "otp";

    public static final String TEMPLATE_EMAIL_NEW_USER = "welcom";

    public static final String TEMPLATE_EMAIL_NEW_SESSION = "new-session";

    public static final String TEMPLATE_EMAIL_DISCIPLINE= "discipline";
    public static final String TEMPLATE_EMAIL_MANGWA = "mangwa";

    public static final String TEMPLATE_EMAIL_END_SESSION = "end-session";

    public static final String TEMPLATE_EMAIL_NEW_ORDER = "new-order";

    public static final String TEMPLATE_EMAIL_ORDER_STOCKAGE = "order-stockage";

    public static final String TEMPLATE_EMAIL_ORDER_SUPPLY = "order-supply";

    public static final String TEMPLATE_EMAIL_ORDER_TRANSFER = "order-transfer";

    public static final String TEMPLATE_EMAIL_DEMANDE_OPPOSITION = "demande-opposition";

    public static final String TEMPLATE_EMAIL_VALID_CREDIT_NOTE = "valid-credit-note";

    public static final String TEMPLATE_EMAIL_CREDIT_NOTE = "credit-note";

    public static final String TEMPLATE_EMAIL_ACCEPT_COUPON = "accept-coupon";

    public static final String TEMPLATE_EMAIL_MODIFY_ORDER = "modify-order";

    public static final String TEMPLATE_EMAIL_END_SEANCE = "end-seance";

    public static final String TEMPLATE_EMAIL_COMPTE_RENDU = "compte-rendu";

    public static final String TEMPLATE_EMAIL_CANCEL_ORDER = "cancel-order";

    public static final String TEMPLATE_EMAIL_CANCEL_MULTI_ORDER = "cancel-multi-order";

    public static final String TEMPLATE_EMAIL_NEW_INVOICE = "new-invoice";

    public static final String TEMPLATE_EMAIL_EXPORT_ORDER_EXCEL = "order-export-excel";

    public static final String TEMPLATE_EMAIL_EXPORT_COUPON_EXCEL = "coupon-export-excel";

    public static final String TEMPLATE_EMAIL_NEW_RECEIVED = "new-received";

    public static final String TEMPLATE_EMAIL_NEW_FACTURE = "new-facture";
    public static final String SUBJECT_PASSWORD_RESET = "ADELI - Réinitialisation du mot de passe";

    public static final String TEMPLATE_PASSWORD_RESET = "email-password-reset";

    public static final String DEFAULT_SIZE_PAGINATION = "20";

    public static final String DEFAULT_TIME_ZONE = "Africa/Douala";



    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
