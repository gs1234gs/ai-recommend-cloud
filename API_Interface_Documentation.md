# AI Recommendation Cloud System - Frontend Backend API Integration Documentation

## 1. System Overview

The AI Recommendation Cloud System is a microservices-based recommendation platform that includes multiple functional modules such as AI intelligent chat, product management, and statistical analysis. This document details the API integration specifications between the frontend and backend services.

## 2. Technical Architecture

### 2.1 Frontend Technology Stack
- React 19
- TypeScript
- Ant Design v5
- Axios
- Vite 7+

### 2.2 Backend Technology Stack
- Spring Boot WebFlux (Reactive Programming)
- R2DBC (Reactive Database Access)
- Microservices Architecture

## 3. Interface Specifications

### 3.1 General Specifications
- All API requests use JSON format
- Unified Bearer Token authentication
- Response format is unified as:
```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 3.2 API Prefixes
- System Management: `/api/sys-api`
- Product Management: `/api/goods-api`
- AI Services: `/api/ai-api`
- Behavior Analysis: `/api/behavior-api`

## 4. AI Chat Service Interfaces

### 4.1 Streaming Chat Interface
- **Request Path**: `POST /api/ai-api/chat/fluxChat`
- **Request Body**:
```json
{
  "prompt": "User input question",
  "sessionId": "Session ID (optional)",
  "userId": "User ID (optional)"
}
```
- **Response**: SSE streaming response, returning result fragments incrementally
- **Purpose**: Implement real-time streaming chat functionality for AI assistant

### 4.2 One-time Chat Interface
- **Request Path**: `POST /api/ai-api/chat/chatAll`
- **Request Body**: Same as streaming chat interface
- **Response**: Complete chat result
- **Purpose**: Obtain complete chat result

### 4.3 Chat Record Management Interfaces
- **Get Chat Records**: `POST /api/ai-api/chat/findChat`
- **Delete Chat**: `DELETE /api/ai-api/chat/deleteById/{id}`
- **Save Chat Title**: `PUT /api/ai-api/chat/saveChat`

## 5. AI Management Service Interfaces

### 5.1 Model Management Interfaces
- **Save/Update Model**: `POST /api/ai-api/bigModel/save`
- **Delete Model**: `DELETE /api/ai-api/bigModel/deleteById/{id}`
- **Paginated Query Models**: `POST /api/ai-api/bigModel/findPage`

### 5.2 Knowledge Base Management Interfaces
- **Save Knowledge Base**: `POST /api/ai-api/knowledge/save`
- **Delete Knowledge Base**: `DELETE /api/ai-api/knowledge/deleteById/{id}`
- **Upload Document**: `POST /api/ai-api/knowledge/uploadDoc`

## 6. Product Management Service Interfaces

### 6.1 Product Management Interfaces
- **Save Product**: `POST /api/goods-api/product/save`
- **Delete Product**: `DELETE /api/goods-api/product/deleteById/{id}`
- **Paginated Query Products**: `POST /api/goods-api/product/findPage`
- **Query Product by ID**: `GET /api/goods-api/product/findById/{id}`

### 6.2 SKU Management Interfaces
- **Save SKU**: `POST /api/goods-api/sku/save`
- **Delete SKU**: `DELETE /api/goods-api/sku/deleteById/{id}`
- **Paginated Query SKU**: `POST /api/goods-api/sku/findPage`

### 6.3 Category Management Interfaces
- **Save Category**: `POST /api/goods-api/category/save`
- **Delete Category**: `DELETE /api/goods-api/category/deleteById/{id}`
- **Paginated Query Categories**: `POST /api/goods-api/category/findPage`

### 6.4 Tag Management Interfaces
- **Save Tag**: `POST /api/goods-api/tag/save`
- **Delete Tag**: `DELETE /api/goods-api/tag/deleteById/{id}`
- **Paginated Query Tags**: `POST /api/goods-api/tag/findPage`

## 7. Statistical Analysis Service Interfaces

### 7.1 User Profile Analysis
- **Get User Profile**: `POST /api/ai-api/statistics/userPortrait`

### 7.2 Activity Analysis
- **Get Activity Data**: `POST /api/ai-api/statistics/activeUsers`
- **Get Retention Rate Data**: `POST /api/ai-api/statistics/retentionRate`

### 7.3 Recommendation Effect Analysis
- **Get Recommendation Effect Data**: `POST /api/ai-api/statistics/recommendationEffect`
- **Get Conversion Rate Data**: `POST /api/ai-api/statistics/conversionRate`

## 8. System Management Service Interfaces

### 8.1 Menu Management Interfaces
- **Get User Menus**: `GET /api/sys-api/sysMenu/userId`
- **Get All Menus**: `GET /api/sys-api/sysMenu/findAll`
- **Save Menu**: `POST /api/sys-api/sysMenu/save`
- **Delete Menu**: `DELETE /api/sys-api/sysMenu/deleteById/{id}`

### 8.2 Role Management Interfaces
- **Paginated Query Roles**: `POST /api/sys-api/sysRole/findPage`
- **Save Role**: `POST /api/sys-api/sysRole/save`
- **Delete Role**: `DELETE /api/sys-api/sysRole/deleteById/{id}`

### 8.3 User Management Interfaces
- **Paginated Query Users**: `POST /api/sys-api/sysUser/findPage`
- **Save User**: `POST /api/sys-api/sysUser/save`
- **Delete User**: `DELETE /api/sys-api/sysUser/deleteById/{id}`

## 9. Frontend Implementation Points

### 9.1 Streaming Chat Processing
```typescript
// Handle streaming AI response
const handleStreamResponse = async (response: Response) => {
  const reader = response.body?.getReader();
  if (reader) {
    const decoder = new TextDecoder();
    let done = false;
    
    while (!done) {
      const { value, done: readerDone } = await reader.read();
      done = readerDone;
      
      if (value) {
        const chunk = decoder.decode(value, { stream: true });
        // Update UI with streaming content
        updateChatContent(chunk);
      }
    }
  }
};
```

### 9.2 Authentication
- Frontend automatically adds Authorization header via HTTP interceptor
- Token stored in SessionStorage
- Automatic redirect to login page on request failure

### 9.3 Error Handling
- Unified error handling mechanism
- User-friendly error messages
- Automatic retry mechanism

## 10. Performance Optimization Suggestions

### 10.1 Frontend Optimization
- Component lazy loading
- Data virtual scrolling
- Image lazy loading
- Request debounce/throttle

### 10.2 Backend Optimization
- Database index optimization
- Caching strategy
- Asynchronous processing
- Connection pool configuration

## 11. Deployment Configuration

### 11.1 Environment Variables
- `VITE_APP_BASE_API`: Backend API base address
- `VITE_APP_PORT`: Frontend service port

### 11.2 Build Commands
- Frontend: `pnpm build`
- Backend: `mvn clean package`

## 12. Common Issues and Solutions

### 12.1 CORS Issues
- Configure cross-origin support on backend
- Frontend proxy configuration

### 12.2 Authentication Expiration
- Automatic token refresh mechanism
- Login status synchronization

### 12.3 Reactive Data Flow
- Properly handle WebFlux responses
- Error propagation mechanism

---

**Document Version**: v1.0  
**Last Updated**: August 2025  
**Author**: AI Recommendation Cloud Development Team