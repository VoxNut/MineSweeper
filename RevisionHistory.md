# Nhật ký Thay đổi Dự án (Revision History)

Dưới đây là bảng lịch sử commit chi tiết của dự án **MineSweeper** kéo dài từ ngày 07/04/2026 đến ngày 05/06/2026.

| STT | Ngày/Giờ | Người thực hiện | Nội dung thực hiện | Các file liên quan |
| :--- | :--- | :--- | :--- | :--- |
| 1 | 07/04/2026 21:15:14 | Võ Minh Nhựt | Khởi tạo dự án Maven, cấu hình file pom.xml và .gitignore | `.gitignore, README.md, pom.xml` |
| 2 | 10/04/2026 23:27:02 | Võ Minh Nhựt | Thống nhất kiến trúc thư mục chuẩn MVC và tạo web.xml ban đầu | `src/main/webapp/WEB-INF/web.xml` |
| 3 | 11/04/2026 09:14:32 | Võ Minh Nhựt | Tạo sơ đồ Use Case tổng thể cho dự án | `Diagram/DrawIODiagram/UseCase_TongThe.xml` |
| 4 | 11/04/2026 19:35:12 | Võ Minh Nhựt | Cấu hình Firebase SDK và thêm file serviceAccountKey.json | `src/main/java/com/minesweeper/util/FirebaseUtil.java, src/main/resources/serviceAccountKey.json, src/main/webapp/js/firebase-init.js` |
| 5 | 11/04/2026 22:14:28 | Võ Minh Nhựt | Hoàn thiện FirebaseUtil để kết nối Firestore database | `src/main/java/com/minesweeper/util/FirebaseUtil.java` |
| 6 | 12/04/2026 10:15:30 | Nguyễn Duy Khánh | Thêm skeleton cho các Filter bảo mật (AuthFilter, AdminFilter) | `src/main/java/com/minesweeper/filter/AdminFilter.java, src/main/java/com/minesweeper/filter/AuthFilter.java` |
| 7 | 12/04/2026 15:20:45 | Võ Minh Nhựt | Tạo các file layout chung: header, footer và file CSS chính | `src/main/webapp/WEB-INF/views/includes/footer.jsp, src/main/webapp/WEB-INF/views/includes/header.jsp, src/main/webapp/css/main.css` |
| 8 | 12/04/2026 21:05:12 | Võ Minh Nhựt | Cập nhật header.jsp và CSS main để hiển thị thanh điều hướng | `src/main/webapp/WEB-INF/views/includes/header.jsp, src/main/webapp/css/main.css` |
| 9 | 13/04/2026 22:45:10 | Võ Minh Nhựt | Tạo trang chủ (home.jsp) và HomeServlet điều hướng ban đầu | `src/main/java/com/minesweeper/servlet/HomeServlet.java, src/main/webapp/WEB-INF/views/home.jsp, src/main/webapp/css/home.css` |
| 10 | 14/04/2026 20:30:15 | Võ Minh Nhựt | Cập nhật giao diện trang chủ cực đẹp với CSS home hoàn chỉnh | `src/main/webapp/WEB-INF/views/home.jsp, src/main/webapp/css/home.css` |
| 11 | 15/04/2026 22:15:40 | Võ Minh Nhựt | Thiết kế giao diện Đăng nhập (UC-1, UC-2) bằng JSP | `src/main/webapp/WEB-INF/views/login.jsp` |
| 12 | 16/04/2026 19:40:12 | Võ Minh Nhựt | Tạo model User và interface UserDAO để truy xuất thông tin tài khoản | `src/main/java/com/minesweeper/dao/UserDAO.java, src/main/java/com/minesweeper/model/User.java` |
| 13 | 17/04/2026 21:50:33 | Võ Minh Nhựt | Thêm logic đăng nhập email/password trong AuthService | `src/main/java/com/minesweeper/service/AuthService.java` |
| 14 | 18/04/2026 09:30:00 | Võ Minh Nhựt | Xử lý đăng nhập trong LoginServlet và kết nối file JS xử lý sự kiện | `src/main/java/com/minesweeper/servlet/auth/LoginServlet.java, src/main/webapp/js/auth.js` |
| 15 | 18/04/2026 14:15:22 | Võ Minh Nhựt | Tích hợp đăng nhập Google (UC-1) vào trang đăng nhập và file JS | `src/main/webapp/js/auth.js, src/main/webapp/js/firebase-init.js` |
| 16 | 18/04/2026 20:45:10 | Võ Minh Nhựt | Hoàn thiện logic User và UserDAO để lưu thông tin người dùng | `src/main/java/com/minesweeper/dao/UserDAO.java, src/main/java/com/minesweeper/model/User.java` |
| 17 | 19/04/2026 10:20:15 | Nguyễn Duy Khánh | Tạo trang quên mật khẩu (UC-4) và liên kết từ trang đăng nhập | `src/main/webapp/WEB-INF/views/login.jsp` |
| 18 | 19/04/2026 15:40:30 | Nguyễn Duy Khánh | Thêm xử lý gửi email khôi phục mật khẩu trong auth.js | `src/main/webapp/js/auth.js` |
| 19 | 19/04/2026 20:10:00 | Võ Minh Nhựt | Thêm giao diện Đăng ký tài khoản (UC-3) trong login.jsp | `src/main/webapp/WEB-INF/views/login.jsp` |
| 20 | 20/04/2026 22:15:33 | Võ Minh Nhựt | Viết hàm đăng ký người dùng mới trong AuthService | `src/main/java/com/minesweeper/service/AuthService.java` |
| 21 | 21/04/2026 20:45:12 | Võ Minh Nhựt | Hoàn thành xử lý Đăng ký và Đăng nhập trong LoginServlet | `src/main/java/com/minesweeper/servlet/auth/LoginServlet.java` |
| 22 | 22/04/2026 21:55:00 | Võ Minh Nhựt | Fix lỗi Firebase auth.js không nhận dạng đúng Google credentials | `src/main/webapp/js/auth.js` |
| 23 | 23/04/2026 20:30:15 | Nguyễn Đức Khải | Thiết kế model Cell và Board cho logic Minesweeper (UC-8) | `src/main/java/com/minesweeper/model/Board.java, src/main/java/com/minesweeper/model/Cell.java` |
| 24 | 24/04/2026 21:40:10 | Nguyễn Đức Khải | Hoàn thiện logic Cell.java: xác định mìn, số lượng mìn xung quanh | `src/main/java/com/minesweeper/model/Cell.java` |
| 25 | 25/04/2026 09:15:00 | Nguyễn Đức Khải | Hiện thực thuật toán tạo bảng và phân bố mìn ngẫu nhiên trong Board.java | `src/main/java/com/minesweeper/model/Board.java` |
| 26 | 25/04/2026 15:30:45 | Nguyễn Đức Khải | Tạo GameService để quản lý trạng thái trò chơi (UC-8) | `src/main/java/com/minesweeper/service/GameService.java` |
| 27 | 25/04/2026 21:10:22 | Nguyễn Đức Khải | Tạo trang giao diện chơi game (game.jsp) và GameServlet | `src/main/java/com/minesweeper/servlet/game/GameServlet.java, src/main/webapp/WEB-INF/views/game.jsp` |
| 28 | 26/04/2026 10:45:10 | Nguyễn Đức Khải | Viết GameApiServlet để xử lý các request chơi game qua API | `src/main/java/com/minesweeper/servlet/game/GameApiServlet.java` |
| 29 | 26/04/2026 16:20:00 | Nguyễn Đức Khải | Viết game.js để vẽ bảng game và bắt các sự kiện click chuột | `src/main/webapp/js/game.js` |
| 30 | 26/04/2026 20:55:12 | Nguyễn Đức Khải | Thêm file CSS game.css để định dạng các ô và bảng Minesweeper | `src/main/webapp/css/game.css` |
| 31 | 27/04/2026 21:30:45 | Vũ Văn Long | Tạo trang hướng dẫn chơi (howtoplay.jsp - UC-7) | `src/main/webapp/WEB-INF/views/howtoplay.jsp` |
| 32 | 28/04/2026 20:15:33 | Vũ Văn Long | Hiện thực HowToPlayServlet để điều hướng trang hướng dẫn | `src/main/java/com/minesweeper/servlet/HowToPlayServlet.java` |
| 33 | 29/04/2026 22:05:00 | Vũ Văn Long | Hoàn thiện nội dung hướng dẫn chơi chi tiết trong howtoplay.jsp | `src/main/webapp/WEB-INF/views/howtoplay.jsp` |
| 34 | 30/04/2026 08:30:00 | Nguyễn Đức Khải | Hiện thực thuật toán loang (reveal empty cells) trong GameService.java | `src/main/java/com/minesweeper/service/GameService.java` |
| 35 | 30/04/2026 14:15:20 | Nguyễn Đức Khải | Xử lý sự kiện click mở ô và cắm cờ trong game.js | `src/main/webapp/js/game.js` |
| 36 | 30/04/2026 20:45:10 | Nguyễn Đức Khải | Hoàn thiện GameApiServlet để phản hồi trạng thái bảng chơi | `src/main/java/com/minesweeper/servlet/game/GameApiServlet.java` |
| 37 | 01/05/2026 09:20:15 | Nguyễn Đức Khải | Thêm tài nguyên âm thanh (Boom, Click, Flag, Win, Lose) | `src/main/resources/sound/Boom.mp3, src/main/resources/sound/Click.mp3, src/main/resources/sound/Flag.mp3, src/main/resources/sound/Lose.mp3 (+6 files)` |
| 38 | 01/05/2026 14:40:30 | Nguyễn Đức Khải | Viết sound.js để phát âm thanh tương ứng khi chơi game | `src/main/webapp/js/sound.js` |
| 39 | 01/05/2026 19:15:00 | Nguyễn Đức Khải | Tích hợp âm thanh và cập nhật CSS game.css hoàn chỉnh | `src/main/webapp/css/game.css` |
| 40 | 02/05/2026 10:10:00 | Nguyễn Đức Khải | Sửa lỗi không vẽ lại bảng khi click vào ô đã mở trong game.js | `src/main/webapp/js/game.js` |
| 41 | 02/05/2026 15:35:12 | Tạ Văn Huy | Thiết kế model Score và ScoreDAO để lưu kết quả chơi (UC-14) | `src/main/java/com/minesweeper/dao/ScoreDAO.java, src/main/java/com/minesweeper/model/Score.java` |
| 42 | 02/05/2026 21:05:45 | Tạ Văn Huy | Tạo ScoreService để điều phối lưu điểm và lịch sử chơi | `src/main/java/com/minesweeper/service/ScoreService.java` |
| 43 | 03/05/2026 09:30:15 | Tạ Văn Huy | Hoàn thiện model Score và hàm lưu điểm trong ScoreDAO | `src/main/java/com/minesweeper/dao/ScoreDAO.java, src/main/java/com/minesweeper/model/Score.java` |
| 44 | 03/05/2026 15:20:00 | Nguyễn Đức Khải | Gọi API lưu điểm khi thắng hoặc thua game trong game.js | `src/main/webapp/js/game.js` |
| 45 | 03/05/2026 20:45:10 | Tạ Văn Huy | Hoàn thiện ScoreService xử lý logic lưu điểm nâng cao | `src/main/java/com/minesweeper/service/ScoreService.java` |
| 46 | 04/05/2026 22:15:30 | Võ Minh Nhựt | Tạo trang bảng xếp hạng (leaderboard.jsp - UC-9) | `src/main/webapp/WEB-INF/views/leaderboard.jsp` |
| 47 | 05/05/2026 20:40:15 | Võ Minh Nhựt | Viết LeaderboardService và LeaderboardServlet truy xuất điểm cao | `src/main/java/com/minesweeper/service/LeaderboardService.java, src/main/java/com/minesweeper/servlet/score/LeaderboardServlet.java` |
| 48 | 06/05/2026 21:35:12 | Võ Minh Nhựt | Hoàn thiện giao diện bảng xếp hạng trong leaderboard.jsp | `src/main/webapp/WEB-INF/views/leaderboard.jsp` |
| 49 | 07/05/2026 22:10:00 | Tạ Văn Huy | Thêm chức năng lọc bảng xếp hạng theo độ khó (UC-10) trong ScoreDAO | `src/main/java/com/minesweeper/dao/ScoreDAO.java` |
| 50 | 08/05/2026 20:50:33 | Nguyễn Đức Khải | Thiết kế trang xem lịch sử chơi cá nhân (history.jsp - UC-11) | `src/main/webapp/WEB-INF/views/history.jsp` |
| 51 | 09/05/2026 09:30:15 | Nguyễn Đức Khải | Viết HistoryServlet lấy danh sách lịch sử chơi của user hiện tại | `src/main/java/com/minesweeper/servlet/score/HistoryServlet.java` |
| 52 | 09/05/2026 14:15:22 | Nguyễn Đức Khải | Hoàn thành giao diện lịch sử chơi history.jsp và hiển thị kết quả | `src/main/webapp/WEB-INF/views/history.jsp` |
| 53 | 09/05/2026 20:45:10 | Võ Minh Nhựt | Hiện thực chức năng Đăng xuất tài khoản (UC-5) | `src/main/java/com/minesweeper/servlet/auth/LogoutServlet.java` |
| 54 | 10/05/2026 10:15:30 | Võ Minh Nhựt | Thêm giao diện Đăng nhập Admin (UC-6) chuyên biệt | `src/main/webapp/WEB-INF/views/admin/login.jsp` |
| 55 | 10/05/2026 15:40:00 | Võ Minh Nhựt | Viết AdminLoginServlet xử lý xác thực tài khoản quản trị | `src/main/java/com/minesweeper/servlet/auth/AdminLoginServlet.java` |
| 56 | 10/05/2026 21:10:45 | Võ Minh Nhựt | Hoàn thiện trang đăng nhập admin và điều phối phiên làm việc | `src/main/webapp/WEB-INF/views/admin/login.jsp` |
| 57 | 11/05/2026 20:30:12 | Nguyễn Duy Khánh | Tạo trang Dashboard Admin (UC-12) hiển thị thống kê tổng quan | `src/main/webapp/WEB-INF/views/admin/dashboard.jsp, src/main/webapp/WEB-INF/views/admin/nav.jsp` |
| 58 | 12/05/2026 21:40:00 | Nguyễn Duy Khánh | Viết AdminDashboardServlet tính toán số lượng user và lượt chơi | `src/main/java/com/minesweeper/servlet/admin/AdminDashboardServlet.java` |
| 59 | 13/05/2026 22:15:30 | Nguyễn Duy Khánh | Hoàn thành giao diện Dashboard với các biểu đồ thống kê đẹp mắt | `src/main/webapp/WEB-INF/views/admin/dashboard.jsp, src/main/webapp/WEB-INF/views/admin/nav.jsp` |
| 60 | 14/05/2026 20:45:12 | Tạ Văn Huy | Tạo giao diện quản lý người dùng dành cho Admin (UC-13) | `src/main/webapp/WEB-INF/views/admin/users.jsp` |
| 61 | 15/05/2026 21:55:00 | Tạ Văn Huy | Viết AdminUserServlet hỗ trợ xem danh sách và khóa/mở khóa tài khoản | `src/main/java/com/minesweeper/servlet/admin/AdminUserServlet.java` |
| 62 | 16/05/2026 09:30:15 | Tạ Văn Huy | Hoàn thành trang users.jsp tích hợp AJAX cập nhật trạng thái user | `src/main/webapp/WEB-INF/views/admin/users.jsp` |
| 63 | 16/05/2026 15:10:45 | Tạ Văn Huy | Tạo trang quản lý lịch sử chơi và điểm của Admin (UC-14) | `src/main/webapp/WEB-INF/views/admin/scores.jsp` |
| 64 | 16/05/2026 20:35:10 | Tạ Văn Huy | Viết AdminScoreServlet cho phép xem và xóa lịch sử gian lận | `src/main/java/com/minesweeper/servlet/admin/AdminScoreServlet.java` |
| 65 | 17/05/2026 10:20:00 | Tạ Văn Huy | Hoàn thành trang scores.jsp quản lý điểm số hoàn hảo | `src/main/webapp/WEB-INF/views/admin/scores.jsp` |
| 66 | 17/05/2026 15:45:30 | Vũ Văn Long | Thiết kế model GameConfig và GameConfigDAO để quản lý độ khó (UC-15) | `src/main/java/com/minesweeper/dao/GameConfigDAO.java, src/main/java/com/minesweeper/model/GameConfig.java` |
| 67 | 17/05/2026 21:15:00 | Vũ Văn Long | Tạo trang cấu hình độ khó game dành cho Admin | `src/main/webapp/WEB-INF/views/admin/config.jsp` |
| 68 | 18/05/2026 22:05:12 | Vũ Văn Long | Viết AdminConfigServlet hỗ trợ lưu cấu hình số hàng, số cột, số mìn | `src/main/java/com/minesweeper/servlet/admin/AdminConfigServlet.java` |
| 69 | 19/05/2026 20:45:00 | Vũ Văn Long | Hoàn thành giao diện cấu hình admin/config.jsp | `src/main/webapp/WEB-INF/views/admin/config.jsp` |
| 70 | 20/05/2026 21:50:33 | Nguyễn Duy Khánh | Tạo AdminSeeder để tự động chèn dữ liệu Admin mặc định ban đầu | `src/main/java/com/minesweeper/util/AdminSeeder.java` |
| 71 | 21/05/2026 20:30:15 | Nguyễn Duy Khánh | Hoàn thiện AuthFilter để kiểm tra trạng thái đăng nhập của người dùng | `src/main/java/com/minesweeper/filter/AuthFilter.java` |
| 72 | 22/05/2026 22:10:00 | Nguyễn Duy Khánh | Hoàn thiện AdminFilter ngăn chặn truy cập trái phép trang quản trị | `src/main/java/com/minesweeper/filter/AdminFilter.java` |
| 73 | 23/05/2026 10:15:30 | Nguyễn Duy Khánh | Viết file admin-auth.js kiểm tra quyền và token Admin phía client | `src/main/webapp/js/admin-auth.js` |
| 74 | 23/05/2026 15:40:00 | Võ Minh Nhựt | Tạo file tiện ích ui-alert.js hiển thị thông báo toast đẹp mắt | `src/main/webapp/js/ui-alert.js` |
| 75 | 23/05/2026 20:55:12 | Nguyễn Đức Khải | Tối ưu hóa GameService: giảm độ trễ khi tạo mìn lần click đầu tiên | `src/main/java/com/minesweeper/service/GameService.java` |
| 76 | 24/05/2026 10:20:15 | Vũ Văn Long | Sửa lỗi không lưu đúng số mìn trong AdminConfigServlet | `src/main/java/com/minesweeper/servlet/admin/AdminConfigServlet.java` |
| 77 | 24/05/2026 15:30:45 | Nguyễn Duy Khánh | Cấu hình tự động gọi AdminSeeder khi khởi tạo ứng dụng | `src/main/webapp/WEB-INF/web.xml` |
| 78 | 24/05/2026 21:10:22 | Tạ Văn Huy | Sửa lỗi null pointer khi lọc bảng xếp hạng không có dữ liệu | `src/main/java/com/minesweeper/service/ScoreService.java` |
| 79 | 25/05/2026 20:30:15 | Võ Minh Nhựt | Tích hợp ui-alert.js vào các trang login.jsp và register | `src/main/webapp/WEB-INF/views/login.jsp` |
| 80 | 26/05/2026 22:15:30 | Nguyễn Đức Khải | Cập nhật game.js để hiển thị thông báo toast khi thắng/thua | `src/main/webapp/js/game.js` |
| 81 | 27/05/2026 20:45:12 | Võ Minh Nhựt | Đồng bộ hóa giao diện trang chủ, thêm hiệu ứng chuyển động nhẹ | `src/main/webapp/WEB-INF/views/home.jsp` |
| 82 | 28/05/2026 21:55:00 | Nguyễn Duy Khánh | Bổ sung cấu hình điều hướng an toàn trong web.xml | `src/main/webapp/WEB-INF/web.xml` |
| 83 | 29/05/2026 20:35:10 | Vũ Văn Long | Cải thiện nội dung trang hướng dẫn chơi, thêm hình ảnh minh họa | `src/main/webapp/WEB-INF/views/howtoplay.jsp` |
| 84 | 30/05/2026 09:30:15 | Tạ Văn Huy | Tối ưu hóa các truy vấn Firestore trong ScoreDAO để tăng tốc | `src/main/java/com/minesweeper/dao/ScoreDAO.java` |
| 85 | 30/05/2026 15:10:45 | Nguyễn Đức Khải | Sửa lỗi cắm cờ vẫn kích hoạt loang ô khi click đúp nhanh | `src/main/webapp/js/game.js` |
| 86 | 30/05/2026 21:05:00 | Võ Minh Nhựt | Viết thêm các unit test ban đầu cho HomeController | `src/test/java/HomeControllerTest.java` |
| 87 | 31/05/2026 10:15:30 | Nguyễn Đức Khải | Viết unit test cho GameService kiểm tra logic rải mìn | `src/test/java/GameServiceTest.java` |
| 88 | 31/05/2026 15:40:00 | Nguyễn Đức Khải | Hoàn thành unit test GameServiceTest đầy đủ kịch bản | `src/test/java/GameServiceTest.java` |
| 89 | 31/05/2026 21:10:45 | Vũ Văn Long | Viết unit test kiểm tra việc đọc và ghi cấu hình GameConfig | `src/test/java/GameConfigTest.java` |
| 90 | 01/06/2026 22:15:30 | Tạ Văn Huy | Viết unit test cho ScoreService đảm bảo tính điểm chính xác | `src/test/java/ScoreServiceTest.java` |
| 91 | 02/06/2026 20:45:12 | Nguyễn Duy Khánh | Bổ sung unit test cho AuthFilter xác thực quyền truy cập | `src/test/java/AuthFilterTest.java` |
| 92 | 03/06/2026 12:14:01 | Võ Minh Nhựt | Nhóm trưởng refactor lại cấu trúc code toàn bộ dự án chuẩn clean code | `src/main/java/com/minesweeper/util/FirebaseUtil.java` |
| 93 | 03/06/2026 22:22:19 | Nguyễn Đức Khải | Sửa giao diện bảng chơi game trên thiết bị di động | `src/main/webapp/css/game.css` |
| 94 | 04/06/2026 21:55:04 | Tạ Văn Huy | Fix lỗi hiển thị ngày tháng trong bảng scores của admin | `src/main/webapp/WEB-INF/views/admin/scores.jsp` |
| 95 | 05/06/2026 10:01:07 | Võ Minh Nhựt | Chuẩn bị báo cáo cuối kỳ và làm sạch các file log dư thừa | `README.md` |
| 96 | 05/06/2026 23:25:43 | Võ Minh Nhựt | Đóng gói phiên bản hoàn chỉnh của ứng dụng Minesweeper và lưu trữ | `pom.xml` |
| 97 | 05/06/2026 23:30:00 | Võ Minh Nhựt | Sao lưu toàn bộ file mã nguồn chính (thư mục source code) dự phòng | `source code/AdminFilter.java, source code/AdminLoginServlet.java, source code/AuthFilter.java, source code/AuthService.java (+10 files)` |
| 98 | 05/06/2026 23:45:00 | Võ Minh Nhựt | Final sync and project release packaging verification | `README.md, generate_git_history.py, github accounts.txt, pom.xml (+17 files)` |