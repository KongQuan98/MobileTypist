import SwiftUI

struct LimelightSpotlightShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.width * 0.25, y: 0))
        path.addLine(to: CGPoint(x: rect.width * 0.75, y: 0))
        path.addLine(to: CGPoint(x: rect.width * 0.95, y: rect.height))
        path.addLine(to: CGPoint(x: rect.width * 0.05, y: rect.height))
        path.closeSubpath()
        return path
    }
}

struct LimelightNavItem: Identifiable {
    let id: Int
    let title: String
    let iconName: String
}

struct LimelightNavView: View {
    let items: [LimelightNavItem]
    @Binding var selectedIndex: Int
    var onTabSelected: ((Int) -> Void)? = nil

    var body: some View {
        GeometryReader { geometry in
            let itemCount = max(items.count, 1)
            let tabWidth = geometry.size.width / CGFloat(itemCount)
            let activeCenterX = (CGFloat(selectedIndex) + 0.5) * tabWidth

            ZStack(alignment: .topLeading) {
                // Background surface
                RoundedRectangle(cornerRadius: 20, style: .continuous)
                    .fill(Color(UIColor.secondarySystemBackground).opacity(0.95))
                    .overlay(
                        RoundedRectangle(cornerRadius: 20, style: .continuous)
                            .stroke(Color.primary.opacity(0.12), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.15), radius: 8, x: 0, y: 4)

                // 1. Limelight Spotlight Light Beam
                LimelightSpotlightShape()
                    .fill(
                        LinearGradient(
                            gradient: Gradient(colors: [Color.accentColor.opacity(0.35), Color.clear]),
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )
                    .frame(width: 68, height: 56)
                    .position(x: activeCenterX, y: 28)
                    .animation(.spring(response: 0.35, dampingFraction: 0.7), value: selectedIndex)

                // 2. Limelight Top Indicator Pill Bar
                Capsule()
                    .fill(Color.accentColor)
                    .frame(width: 42, height: 4)
                    .position(x: activeCenterX, y: 4)
                    .animation(.spring(response: 0.35, dampingFraction: 0.7), value: selectedIndex)

                // 3. Navigation Items Row
                HStack(spacing: 0) {
                    ForEach(0..<items.count, id: \.self) { index in
                        let isSelected = index == selectedIndex
                        let item = items[index]

                        Button(action: {
                            withAnimation(.spring(response: 0.35, dampingFraction: 0.7)) {
                                selectedIndex = index
                                onTabSelected?(index)
                            }
                        }) {
                            VStack(spacing: 2) {
                                Image(systemName: item.iconName)
                                    .font(.system(size: 18, weight: isSelected ? .bold : .regular))
                                if !item.title.isEmpty {
                                    Text(item.title)
                                        .font(.system(size: 10, weight: isSelected ? .bold : .medium))
                                }
                            }
                            .foregroundColor(isSelected ? .accentColor : .primary.opacity(0.5))
                            .opacity(isSelected ? 1.0 : 0.5)
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                        }
                    }
                }
            }
        }
        .frame(height: 64)
        .padding(.horizontal, 16)
    }
}
