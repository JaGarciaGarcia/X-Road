# X-Road Security Architecture

**Technical Specification**

Version: 0.3  
27.06.2019

Doc. ID: ARC-SEC

---

## Version history

 Date       | Version | Description                                                 | Author
 ---------- | ------- | ----------------------------------------------------------- | --------------------
 20.06.2019 | 0.1     | Initial version                                             | Niall O’Donoghue
 27.06.2019 | 0.2     | Converted to Github flavoured Markdown                      | Petteri Kivimäki
 28.06.2019 | 0.3     | Editorial changes                                           | Petteri Kivimäki
  
## Table of Contents

<!-- toc -->

- [License](#license)
- [1 Introduction](#1-introduction)
  * [1.1 Terms and Abbreviations](#11-terms-and-abbreviations)
  * [1.2 References](#12-references)
- [2 Environment Assumptions](#2-environment-assumptions)
- [3 Confidentiality](#3-confidentiality)
- [4 Integrity](#4-integrity)
- [5 Availability](#5-availability)
- [6 Authentication](#6-authentication)
- [7 Access Control](#7-access-control)
  * [7.1 Messaging Access Control](#7.1-messaging-access-control)
  * [7.2 Web UI Access Control](#7.2-web-ui-access-control)
- [8 Input Validation](#8-input-validation)
  * [8.1 Web UI Input Validation](#8.1-web-ui-input-validation)
  * [8.2 Messaging Validation](#8.2-messaging-validation)
- [9 Logging](#9-logging)
- [10 Time-Stamping](#10-time-stamping)
- [11 Updatability](#11-updatability)
- [12 Trust Federation](#12-trust-federation)
- [13 Standardised Protocols](#13-standardised-protocols)
- [14 Central Server and Security Components](#14-central-server-and-security-components)
  * [14.1 Signer Component](#14.1-signer-component)
  * [14.2 Secure Signature Creation Device](#14.2-secure-signature-creation-device)
  * [14.3 Password Store](#14.3-password-store)
  * [14.4 Database](#14.4-database)
  * [14.5 User Interface](#14.5-user-interface)
- [15 Security Server Roles and Components](#15-security-server-roles-and-components)
  * [15.1 Security Server Roles](#15.1-security-server-roles)
    * [15.1.1 Access Rights](#15.1.1-access-rights)
  * [15.2 Security Server Components](#15.2-security-server-components)
    * [15.2.1 Proxy](#15.2.1-proxy)
    * [15.2.2 Message Log](#15.2.2-message-log) 
 - [16 Certificates and Keys Management](#16-certificates-and-keys-management) 
 - [17 Monitoring](#17-monitoring)
   * [17.1 Controlling Access to Monitoring](#17.1-controlling-access-to-monitoring) 
 - [18 Privacy](#18-privacy)
   * [18.1 Purpose Limitation](#18.1-purpose-limitation)
   * [18.2 Data Mimimisation](#18.2-data-mimimisation) 
 - [19 Regulatory Compliance](#19-regulatory-compliance)
   * [19.1 Common Regulations](#19.1-common-regulations)
   * [19.2 Environment and Country-Specific Regulations](#19.2-environment-and-country-specific-regulations)
 - [20 Appendix](#20-appendix)
   * [20.1 Unique Identifiers](#20.1-unique-identifiers)
   * [20.2 Trust Services](#20.2-trust-services) 

<!-- tocstop -->

## License

This document is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/

## 1 Introduction

X-Road is an open source data exchange layer solution that enables organizations to exchange information over the Internet. X-Road is a centrally managed distributed data exchange layer between information systems that provides a standardized and secure way to produce and consume services. For a more in-depth introduction to X-Road, refer to the X-Road Architecture \[[ARC-G](../Architecture/arc-g_x-road_arhitecture.md)\].

This document describes the X-Road security architecture and how it fulfills security and privacy principles and best practices. Technical descriptions and guides for X-Road components and protocols are found in separate documents. 

Figure 1 X-Road Security Architecture depicts the X-Road environment and its actors and the data exchanges between them. 

<a id="_X_Road_security_architecture" class="anchor"></a>
![](img/arc-sec_x-road_security_architecture_diagram.png)

Figure 1. X-Road security architecture.

The identity of each organization (X-Road Service Provider or Service Consumer) and technical entry point (Security Server) is verified using certificates that are issued by a trusted Certification Authority (CA) when an organization joins an X-Road ecosystem. The identities are maintained centrally, but all the data is exchanged directly between a consumer and provider. Message routing is based on organization and service level identifiers that are mapped to physical network locations of the services by X-Road. All the evidence regarding data exchange is stored locally by the data exchange parties, and no third parties have access to the data. Time-stamping and digital signature together guarantee non-repudiation of the data sent via X-Road.

### 1.1 Terms and Abbreviations

See X-Road terms and abbreviations documentation \[[TA-TERMS](#Ref_TERMS)\].


### 1.2 References

1. <a id="Ref_ARC-G" class="anchor"></a>\[ARC-G\] Cybernetica AS. X-Road Architecture. Document ID: [ARC-G](arc-g_x-road_arhitecture.md).
2. <a id="Ref_TERMS" class="anchor"></a>\[TA-TERMS\] X-Road Terms and Abbreviations. Document ID: [TA-TERMS](../terms_x-road_docs.md).
3. <a id="Ref_PKCS10" class="anchor"></a>\[PKCS10\] Certification Request Syntax Standard. RSA Laboratories, PKCS \#10.
4. <a id="Ref_UG-SS" class="anchor"></a>\[UG-SS\] Cybernetica AS. X-Road 6. Security Server User Guide. Document ID: [UG-SS](../Manuals/ug-ss_x-road_6_security_server_user_guide.md)
5. <a id="Ref_UG-SS" class="anchor"></a>\[UG-CS\] Cybernetica AS. X-Road 6. Central Server User Guide. Document ID: [UG-CS](../Manuals/ug-cs_x-road_6_central_server_user_guide.md)
6. <a id="Ref_EIDAS" class="anchor"></a>\[EIDAS\] EU Regulation No 910/2014 – Regulation (EU) No 910/2014 of the European Parliament and of the Council of 23 July 2014 on electronic identification and trust services for electronic transactions in the internal market and repealing Directive 1999/93/EC
7. <a id="Ref_BATCH-TS" class="anchor"></a>\[BATCH-TS\] Freudenthal, Margus. Using Batch Hashing for Signing and Time-Stamping. Cybernetica Research Reports, T-4-20, 2013.
8. <a id="Ref_UC-FED" class="anchor"></a>\[UC-FED\] Cybernetica AS. X-Road 6. Use Case Model for Federation. Document ID: [UC-FED](../UseCases/uc-fed_x-road_use_case_model_for_federation_1.1_Y-883-7.md)
9. <a name="ARC-CS"></a>\[ARC-CS\]-- X-Road: Central Server Architecture. Document ID: [ARC-CS](arc-cs_x-road_central_server_architecture.md). 
10. <a id="Ref_ARC-ENVMON" class="anchor"></a>\[ARC-ENVMON\] X-Road: Monitoring Architecture. Document ID: [ARC-ENVMON](../EnvironmentalMonitoring/Monitoring-architecture.md).
11. <a id="Ref_GDPR" class="anchor"></a>\[GDPR\] EU Regulation No 679/2016 – Regulation (EU) 2016/679 of the European Parliament and of the Council of 27 April 2016 on the protection of natural persons with regard to the processing of personal data and on the free movement of such data, and repealing Directive 95/46/EC

## 2 Environment Assumptions

X-Road facilitates a data bridge infrastructure between a variety of organisational actors, such as government registers, financial institutions, and telecommunications service providers. X-Road is therefore a critical information infrastructure (CII) system essential for the operation and sustainability of data exchange between such X-Road member organisations. Disruption of CII may be caused by a variety of human-induced actions or technical failures. 

X-Road security is therefore designed with CII-equivalence resilience in mind. Organisations must register with and be affiliated to X-Road, and acquire identity and signing certificates and keys, before they can perform data exchange. 

## 3 Confidentiality

For compliance with the security principle of confidentiality, the objective is to limit visibility of X-Road assets (organisational data) to the actors (registered X-Road organisational members) that are authenticated and authorised to see the data. With assurance of confidentiality, the threat being mitigated is the unintended revealing of X-Road assets to unauthorised third parties. 
    
X-Road messages transmitted over the public Internet are secured using digital signatures and encryption. The motivation for bidirectional HTTP over Transport Layer Security (TLS) is to enforce anti-eavesdropping and anti-tampering protections to ensure the integrity and privacy of the messages exchanged between X-Road actors. X-Road-internal TLS certificates are used for setting up the TLS connection between the Security Server and information systems that provide and consume services. 

## 4 Integrity

For compliance with the security principle of integrity, the objective is to ensure that X-Road assets are not modified and do not become corrupted. With assurance of integrity, the threat being mitigated is unauthorised access to and unauthorised actions upon X-Road assets. 

X-Road incorporates a public key infrastructure (PKI) whereby a certification authority (CA) issues authentication certificates to Security Servers and signing certificates to X-Road member organisations. The CA processes certificate signing requests conforming to \[[PKCS10](#Ref_PKCS10)\].

All X-Road messages are signed by the signing key of the organisations that send the messages and all messages are logged. Message logging is enabled by default. This means that both message headers and message bodies are logged. Logging of message bodies may be disabled on Security Server level or for selected subsystems. The logs are stored as plaintext on the Security Server.

## 5 Availability

For compliance with the security principle of availability, the objective is to ensure that X-Road assets are readily available to authorised X-Road actors that require them. With assurance of availability, the threat being mitigated is the denial to authorised actors of X-Road services.

Availability is a cornerstone of critical infrastructure. X-Road is design so that no component is a system-wide bottleneck or point of failure. 

X-Road Security Servers incorporate denial-of-service mitigation functionality. X-Road Linux services will automatically restart after a local system crash. 

To fortify the availability of the entire X-Road system, the service consumer’s/user's and service provider's Security Servers may be set up in a redundant configuration as follows:

  * One service user can use multiple Security Servers in parallel to perform requests.
  * If a service provider connects multiple Security Servers to the network to provide the same services, the requests are load-balanced amongst the Security Servers.
  * If one of the service provider's Security Servers goes offline, the requests are automatically redirected to other available Security Servers.

## 6 Authentication

For compliance with the security principle of authentication, the objective is to ensure that the provenance (identity) of the X-Road asset or X-Road actor is known and verified. This is accomplished in a standardised manner using authentication keys and certificates. With assurance of integrity, the threat being mitigated is unauthorised access to X-Road infrastructure and assets therein.

X-Road enforces organisation-level authentication (and authorization) mechanisms and for X-Road Administrator web application frontend-to-backend connections and direct calls to the backend for configuration and maintenance automation purposes.

An X-Road organisation’s client information system Security Server acts as the entry point to all the X-Road services. The client information system is responsible for implementing a user authentication and access control mechanism that complies with the requirements of the particular X-Road instance. The identity of the end user may be made available to the service provider by including it in the service request. 

For details on X-Road Administrator web application user management-related authentication, refer to \[[UG-SS](#Ref_UG-SS)\] section 2.

For details on configuring a Security Server’s authentication key and certificate, refer to \[[UG-SS](#Ref_UG-SS)\] section 3.2.

For details on registering a Security Server’s authentication key and certificate in the Central Server, refer to \[[UG-CS](#Ref_UG-SS)\] section 8.3.

## 7 Access Control
 
For compliance with the security principle of least privilege, the objective is to ensure that X-Road actors, processes and controls must be able to access only the X-Road information and resources that are limited to and necessary for the legitimate and intended purpose.

### 7.1 Messaging Access Control

X-Road core handles access control on the organisation level during data exchange between registered X-Road members. 

### 7.2 Web UI Access Control

When the end user is successfully authenticated, least privilege-based access control is enforced for access to system resources whereby the frontend receives information about current user's roles and permissions using /api/user resource. The backend defines authorisation rules based on permissions.

Details on Security Server user roles and associated access controls are described in section 15.1 Security Server Roles.

In X-Road, a class-level `PreAuthorize("denyAll")` annotation is enforced to forbid access to all methods, so that access control starts by denying all access by default, and so that access will not be allowed to all roles if a new resource is added yet authorisation is somehow configured incorrectly.

## 8 Input Validation

For compliance with the principle of sanitised input, it is security best practice to validate all inputs at the server. X-Road has two validation aspects; a) web UI input validation and b) messaging validation.

### 8.1 Web UI Input Validation

User input parsing is enforced in the Central Server UI and the Security Server UI, whereby there is removal of leading and trailing whitespaces, verification that that all mandatory fields are filled, and verification that the user input does not exceed 255 characters.

If one or more mandatory fields are not filled, it results in a “Missing parameter: 'X'" error message. If  user input exceeds 255 characters, it results in a “Parameter 'X' input exceeds 255 characters” error message.

### 8.2 Messaging Validation 

When input contains XML, it must be validated against its schema before using it. XML injection attacks are mitigated by ensuring that XML input follows the rules specified in the schema. Down-stream errors that might be caused from invalid XML input are mitigated by validating the XML at the earliest point where it crosses a trust boundary.

## 9 Logging

For compliance with the security principle of non-repudiation, all messages processed by X-Road are usable as digital evidence. The technical solution complies with requirements for digital seals according to regulation for electronic identification and trust services for electronic transactions \[[EIDAS](#Ref_EIDAS)\]. EIDAS defines two levels for digital seals: 1) advanced and 2) qualified. Qualified digital seals require that a hardware security module (HSM) device must be used for storing private keys and the CA issuing the certificates must be present in the EU’s list of trusted trust service providers. X-Road supports HSMs and X-Road operators can choose the CAs that are used in their environments. If these requirements are not met, then the digital seals created by X-Road are advanced instead of qualified.

X-Road incorporates the following logs:

  * **Audit log** – log where user-configured changes to the system state or configuration (via the user interface) are logged, regardless of whether the outcome was a success or failure.
  * **Message log** – provides the means to prove the reception of a regular request or response message to a third party. Messages exchanged between Security Servers are signed and encrypted. For every regular request and response, the Security Server produces a complete signed and timestamped document. Messages are logged and provided with a batch signature. The purpose of the message log is to provide the means to prove to a third party the reception of a request/response message. The Security Server messagelog saves each request and response message sent through the Security Server to the messagelog database. There is one log record inserted per transaction. Periodically (by default every six hours), the log archiver reads all non-archived records from the database, writes them to disk, and updates the records in the database, marking them as archived. Every twelve hours, the log cleaner executes a bulk delete removal of all archived records that are older than a configurable age; the default is thirty days. Message archiving interval lengths are configurable via configuration settings. The Security Server administrator is responsible for transferring the archived log files into long term storage. Such storage components are organisation-specific.
  * **System service log** – log which is made from a running system service of a Security Server, for example from xroad-confclient, -jetty, -proxy, signer services.

If a message log audit is required, message logs for some time period may be queried; this creates a zip file that contains the logs in a tamper-resistant format (signed hash of the log tree). 

## 10 Time-Stamping

Also related to the security principle of non-repudiation (and integrity), a time-stamping authority enforces use of a time-stamping protocol by Security Servers to ensure long-term proof value of exchanged messages. The issued time stamps certify the existence of the messages at a certain point of time and the Security Servers log all of the messages and their signatures. These logs are periodically time-stamped to create long-term proof.

X-Road uses batch time-stamping (refer to \[[BATCH-TS](#Ref_BATCH-TS)\]). This reduces the load of the time-stamping service. The load does not depend on the number of messages exchanged over the X-Road, rather it depends on the number of Security Servers in the system.

X-Road supports creating time-stamps synchronously for each message too. Using synchronous time-stamping may be a security policy requirement to guarantee the time-stamp at the time of logging the message. However, batch time-stamping is the default for performance and availability reasons.

## 11 Updatability

X-Road is designed to enable reliable installation of software updates including security updates. X-Road software packages are signed so that their origins are traceable. 

## 12 Trust Federation

The trust federation of X-Road instances allows for the members of one X-Road instance to use the services provided by members of the other instance, thus making the X-Road systems interoperable.

To make the federating systems aware of each other, the external configuration anchor of the federation partner must be uploaded as a trusted anchor to the Central Servers of the federating X-Road instances.

The trusted anchors are distributed to the Security Servers as a part of the internal configuration. The Security Servers use the trusted anchors to download external configuration from the federation partners. The external configuration contains the information that the Security Servers of the partner instances need to communicate with each other.

To end a federation relationship with an X-Road instance, the trusted anchor of that instance must be deleted from the Central Server.

For further information on X-Road Trust Federation, refer to \[[UC-FED](#Ref_UC-FED)\].

## 13 Standardised Protocols

For compliance with security principle of economy of mechanism, X-Road member organizations are not required to implement security-dependent methods for data exchange; they are able to connect to any number of service providers via the following standardized protocols that ensure security-supportive functional consistency. For details of X-Road protocols, refer to the Technical Architecture \[[ARC-G](#Ref_ARC-G)\]. Summaries of the protocols are as follows:

  * Message Protocol is used by service client and service provider information systems for communicating with the X-Road Security Server.
  * Message Transport Protocol is used by Security Server to exchange service requests and service responses. The protocol is based on HTTPS and uses mutual certificate-based TLS authentication.
  * Configuration Download Protocol is a synchronous protocol that is offered by the Central Server. Configuration clients download the generated global configuration files from the Central Server. It is used by configuration clients such as Security Servers and configuration proxies.
  * Service Metadata Protocol may be used by the service client information systems to gather information about the X-Road instance and may be used to find X-Road members.
  * Download Signed Document Protocol may be used by the information systems to download signed containers from the Security Server's message log. In addition, the service provides a convenience method for downloading global configuration that may be used to verify the signed containers.
  * Management Services Protocol is used by Security Servers to perform management tasks such as registering a Security Server client or deleting an authentication certificate. The management services are implemented as standard X-Road services that are offered by the organization managing the X-Road instance. The exception is the authentication certificate registration service that is implemented directly by the Central Server. 
  * Online Certificate Status Protocol (OCSP) is used by the Security Servers to query the validity information about the signing and authentication certificates. OCSP protocol is a synchronous protocol that is offered by the OCSP responder belonging to a certification authority. In X-Road, each Security Server is responsible for downloading and caching the validity information about its certificates. The OCSP responses are sent to the other Security Servers as part of the message transport protocol to ensure that the Security Servers do not need to discover the OCSP service used by the other party. 
  * Time-Stamping Protocol is used by Security Servers to ensure long-term proof value of  exchanged messages. The Security Servers log all messages and their signatures. These logs are periodically time-stamped to create long-term proof. Time-stamping is used in an asynchronous manner, so temporary unavailability of the time-stamping service does not directly affect the X-Road message exchange.

## 14 Central Server and Security Components

The Central Server manages the database of X-Road members and Security Servers. In addition, the Central Server contains the security policy of the X-Road instance. The security policy consists of the following: 

  * list of trusted certification authorities,
  * list of trusted time-stamping authorities,
  * tuneable parameters such as maximum allowed lifetime of an OCSP response.
   
Both the member database and the security policy are made available to the Security Servers via the HTTP protocol. This distributed set of data forms the global configuration. The Central Server incorporates the following security-centric components.

### 14.1 Signer Component

The signer component is responsible for managing the keys and certificates used for signing the global configuration. The signer is called from the management services component to create the signature for the configuration.

### 14.2 Secure Signature Creation Device

The SSCD (Secure Signature Creation Device) is an optional hardware security module (HSM) component that provides secure cryptographic signature creation capability to the signer. The SSCD needs to be a PKCS #11 compliant hardware device that may be optionally used by the Central Server for signing the generated global configuration files it generates. The use of the interface requires that a PKCS #11 compliant device driver is installed and configured in the Central Server system. SSCDs can also be used in conjunction with Security Servers.

### 14.3 Password Store

The Password Store stores security token passwords in a shared memory segment of the operating system that may be accessed by the Central Server interface and signer. Allows security token logins to persist, until the Central Server is restarted, without compromising the passwords.

### 14.4 Database

The Central Server holds the X-Road configuration in a PostgreSQL database. The database contains the security policy of the X-Road instance as well as a list of members, Security Servers, global groups and management services. 

### 14.5 User Interface

The Central Server user interface allows the user to manage X-Road members and Security Servers and define the global configuration parameters that are distributed to the Security Servers. User action events that change the system state or configuration are logged into the audit log. The actions are logged regardless of whether the outcome was a success or a failure. 

For other Central Server details, refer to \[[ARC-CS](#ARC-CS)\].

## 15	Security Server Roles and Components

The main function of a Security Server is to mediate requests in a way that preserves their evidential value. The Security Server is connected to the public Internet from one side and to the information system within the organization's internal network from the other side (refer to Figure 1 X-Road Security Architecture). The Security Server is equipped with the functionality required to secure the message exchange between a client and a service provider.

A Security Server instance is an independent and separately identifiable entity. A Security Server identity consist of a server identifier (member id + server code). For each server identifier there may be multiple authentication certificates present locally, each of which must be unique. However, only one authentication certificate must be active and registered on the Central Server at a time. In addition, each Security Server has an address (DNS name or IP address) which is not required to be unique. The global configuration binds together the authentication certificate(s), server identifier and address. The authentication certificate may contain information about the service identifier; however this is optional. Also, the server address and the common name or alternate subject names in the authentication certificate may be different.

Messages transmitted over the public Internet are secured using digital signatures and TLS (HTTPS) encryption. The service provider's Security Server applies access control to incoming messages, thus ensuring that only those users that have signed an appropriate agreement with the service provider can access the data.

### 15.1 Security Server Roles

Security servers support the following user roles (refer to \[[UG-SS](#Ref_UG-SS)\] for more details):

  * Security Officer is responsible for the application of the security policy and security requirements, including the management of key settings, keys, and certificates.
  * Registration Officer is responsible for the registration and removal of Security Server clients.
  * Service Administrator manages the data of and access rights to services
  * System Administrator is responsible for the installation, configuration, and maintenance of the Security Server.
  * Security Server Observer can view the status of the Security Server without having access rights to edit the configuration. This role may be used to offer users read-only access to the Security Server admin user interface.

User management is performed on command line in root user permissions. One user may have multiple roles and multiple users may be in the same role. Each role has a corresponding system group, created upon the installation of the system.

#### 15.1.1 Access Rights

If the logged-in user does not have a permission to carry out a particular task, the button that would initiate the action is hidden. Nor is it possible to run the task using its corresponding keyboard combinations or mouse actions. Only the permitted data and actions are visible and available to the user. This fulfils the security principle of least privilege.

### 15.2 Security Server Components

#### 15.2.1 Proxy

The proxy is responsible for mediating messages between service clients and service providers. The messages are transmitted over the public Internet and the proxy ensures that the communication is secured using digital signatures and encryption. The component is a standalone Java daemon application.

#### 15.2.2 Message Log

The purpose of the message log is to provide means to prove the reception of a regular request or response message to a third party. Messages exchanged between Security Servers are signed and encrypted. For every regular request and response, the Security Server produces a complete signed and timestamped document  Messages are stored with their signatures and signatures are timestamped. 

## 16 Certificates and Keys Management

The signer component is responsible for managing the keys and certificates used for signing the global configuration. The signer is called from the management services component to create the signature for the configuration.

By default, X-Road utilises 2048 bit RSA keys as authentication and signing keys/certificates. The key length may be configured using the Security Server system parameters. Longer keys may be utilised in X-Road without compatibility issues; 2k, 3k and 4k keys may be simultaneously utilised.

## 17 Monitoring

X-Road monitoring is conceptually split into environmental and operational monitoring. The X-Road monitoring service uses several sensors to collect the data; the CertificateInfoSensor sensor produces the following security certificate-associated monitoring data:
   
  * data: information about certificates associated with this Security Server
    * certificate SHA-1 hash
    * validity period: not before (ISO 8601 date)
    * validity period: not after (ISO 8601 date)
    * the type of the certificate:
      * `AUTH_OR_SIGN` for the Security Server member certificates (for signing) and the Security Server certificate (for authentication)
      * `INTERNAL_IS_CLIENT_TLS` for the client Information system certificates
      * `SECURITY_SERVER_TLS` for the TLS certificate of the Security server
      * is the certificate active (true/false)
    * data is refreshed once per day

### 17.1 Controlling Access to Monitoring 

Monitoring queries are allowed from
  * a client that is the owner of the Security Server
  * a central monitoring client (if any have been configured)

The central monitoring client is configured via Central Server administrator user interface. Attempts to query monitoring data from other clients results in an AccessDenied system response.

For more in-depth technical details of the X-Road Monitoring Architecture, refer to \[[ARC-ENVMON](#Ref_ARC-ENVMON)\].

## 18 Privacy

Security best practice supports and facilitates privacy best practice. Privacy involves Personally Identifiable Information (PII) which is any data (including IP addresses) that allow the identification of a person, any data that the person has disclosed to an X-Road operator, or the person’s or other person’s data that are in their possession, including Personal data. 

X-Road is obligated to comply with the General Protection Data Regulation (GDPR) that stipulates how personal data must be processed in any operation performed on personal data, including collection, recording, organization, storage, alteration, disclosure, granting access to personal data, consultation and retrieval, use of personal data, communication, cross-usage, combination, closure, erasure, destruction, or several of the aforementioned operations, regardless of the manner in which the operations are carried out or the means used.

### 18.1 Purpose Limitation

X-Road data is communicated, processed and stored only for the specified, explicit and legitimate intended purposes and not in any manner that is incompatible with X-Road data purposes and X-Road security policy. 

### 18.2 Data Mimimisation

X-Road data is limited to what is adequate, relevant and necessary in relation to the purposes for which data are processed.

## 19 Regulatory Compliance

X-Road is obligated to comply with security requirements stipulated by the following regulatory bodies:

### 19.1 Common Regulations

X-Road complies with the following common European Union (EU) regulations:

  * EIDAS – Regulation (EU) No 910/2014 of the European Parliament and of the Council of 23 July 2014 on electronic identification and trust services for electronic transactions in the internal market. Refer to \[[EIDAS](#Ref_EIDAS)\]. 
  * GDPR – General Data Protection Regulation (EU) 2016/679 of the European Parliament and of the Council of 27 April 2016 on the protection of natural persons with regard to the processing of personal data and on the free movement of such data. Refer to \[[GDPR](#Ref_GDPR)\].

### 19.2 Environment and Country-Specific Regulations

X-Road complies with the following environment and country-specific regulations:

  * VAHTI – Information security standard that is developed for the Finnish public sector. VAHTI is compulsory for Finnish state and local government organisations who handle databases/registers.
  * ISKE - Information security standard that is developed for the Estonian public sector. ISKE is compulsory for Estonian state and local government organisations who handle databases/registers.

## 20 Appendix 

### 20.1 Unique Identifiers

**Central service identifier** – identifier, that uniquely identifies service in X-Road network without having a reference for service provider. Central service identifier consists of X-Road instance identifier and central service code.

**Global access group identifier** – identifier, that uniquely identifies access group in X-Road Network. Global access group identifier consists of X-Road instance identifier and global group code.

**Local access group identifier** – identifier, that uniquely identifies access group for a Security Server client. Global access group identifier consists of X-Road instance identifier and global group code.

**Member class** – identifier, that is identified by the X-Road governing authority and that uniquely identifies members with similar characteristics. All members with the same member class must be uniquely identifiable by their member codes.

**Member code** – identifier, that uniquely identifies an X-Road member within its member class. The member code remains unchanged during the entire lifetime of the member.

**Member identifier** – identifier, that uniquely identifies a member in the X-Road Network. Member identifier consists of X-Road instance identifier, member class, and member code.

**Security server code** – identifier, that uniquely identifies the Security Server in all of the Security Servers of the Security Server owner.

**Security server identifier** – identifier, that uniquely identifies Security Server in X-road Network. The Security Server identifier consists of Security Server owner identifier and Security Server code.

**Service identifier** – identifier, that uniquely identifies service in X-Road Network. The service identifier consists of member identifier of the service provider, service code and version of the service. Including version of the service in the service identifier is optional.

**Subsystem code** – code, that uniquely identifies subsystem in all of the subsystems of the member.

**Subsystem identifier** – identifier, that uniquely identifies subsystem in X-Road Network. Subsystem identifier consists of member identifier and subsystem code.

**X-Road instance identifier** – identifier, that uniquely identifies the X-road instance in the X-Road Network.

### 20.2 Trust Services

**Approved certification service provider** – Provider of a trust service approved on X-Road, who provides at least following trust services approved on X-Road: service of authentication certificate of Security Server, service of signature certificate of a member, and certificate validation service (OCSP).

**Approved timestamp service provider** – Provider of a trust service approved on X-Road, who provides the timestamp service.

**Authentication certificate of Security Server** – qualified certificate of e-stamp issued by certification service provider approved on X-Road and bound to Security Server, certifying authenticity of Security Server and used for authentication of Security Servers upon establishment of connection between Security Servers. Upon establishment of connection, it is checked from global configuration, if the Security Server trying to establish connection has registered the used authentication certificate in X-Road governing authority (i.e. the used authentication certificate is bound to the ID of Security Server).

**Certification authority (CA)** – is an entity that issues digital certificates. A digital certificate certifies the ownership of a public key by the named subject of the certificate.

**Certification service CA** – is used in the X-Road system as a trust anchor for a certification service. The certification service CA may, but does not have to be a Root CA.

**Certificate signing request (CSR)** – is generated in the Security Server for a certain approved certification authority for signing a public key and associated information.

**Internal TLS certificates** – are used for setting up the TLS connection between the Security Server and the client information systems.

**Signature certificate of a member** – qualified certificate of e-stamp issued by certification service provider approved on X-Road and bound to a member, used for verification of the integrity of mediated messages and association of the member with the message.

**Timestamp** – means data in electronic form which binds other data in electronic form to a particular time establishing evidence that the latter data existed at that time (EU No 910/2014)

**Timestamping authority (TSA)** – is an entity that issues timestamps. Timestamps are used to prove the existence of certain data before a certain point of time without the possibility that the owner can backdate the timestamps.

**TLS certificate** – is a certificate used by the Security Server to authenticate the information system when HTTPS protocol is used for connections between the service client's or service provider's Security Server and information system.

**Validation service (OCSP)** – Validation service of the validity of certificate issued by certification service provider approved on X-Road.

**Trusted anchor** – is a configuration anchor that points to the external configuration source of a federation partner and has been uploaded to the Central Server during the federation process. Trusted anchors are distributed to the configuration clients of the local X-Road system as a part of private parameters.